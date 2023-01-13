package com.example.alimka.BotApi.util;

import com.example.alimka.BotApi.BotState;
import com.example.alimka.Cache.UserDataCache;
import com.example.alimka.Service.ReplyMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.*;
@Component
public class FilterOfStates {
    private UserDataCache userDataCache;
    private ReplyMessageService replyMessageService;
    private EventsDeterminator eventsDeterminator;

    /* 0 - days
    *  1 - current
    *  2 - TES
    *  3 - TMPH
    *  4 - PO
    *  5 - YOUR LEVEL
    *  6 - REACH LEVEL
    *  7 - essence per day*/


    public FilterOfStates(UserDataCache userDataCache, ReplyMessageService replyMessageService, EventsDeterminator eventsDeterminator) {
        this.userDataCache = userDataCache;
        this.replyMessageService = replyMessageService;
        this.eventsDeterminator = eventsDeterminator;
    }

    private void setToUserModeList(String mode, long userId){
        List<Integer> putValuesList = new ArrayList<>();

        switch(mode){
            case "labyrint":
                putValuesList = Arrays.asList(0, 1);
                break;
            case "tournament":
                putValuesList = Arrays.asList(0, 1, 3);
                break;
            case "po":
                putValuesList = Arrays.asList(0, 1, 4);
                break;
            case "twisted":
                putValuesList = Arrays.asList(0, 1, 2);
                break;
            case "essence":
                putValuesList = Arrays.asList(5, 6, 7);
                break;
            case "fill info":
                putValuesList = Arrays.asList(0, 1, 2, 3, 4);
                break;
            case "bait":
                putValuesList = Arrays.asList(0, 1);
                break;
        }


        userDataCache.setModeOfUser(userId, putValuesList);
    }

    public SendMessage filterModeState(Message message, List<BotState> modeStates, List<String> questions, String mode) {
        String userAnswer = message.getText();
        long chatId = message.getChatId();
        long userId = message.getFrom().getId();


        BotState stateOfUser = userDataCache.getUsersCurrentBotState(userId);
        setToUserModeList(mode, userId);
        SendMessage replyToUser = null;

        if (isFirstMessageOfCommand(stateOfUser, modeStates)) {
            nextState(userId, modeStates);
            replyToUser = replyMessageService.getReplyMessage(chatId, questions.get(userDataCache.getUsersCounterState(userId)));
            return replyToUser;
        }

        boolean hasException = processInputNumber(userAnswer, userId, mode) != "pass";

        if(hasException){
            userDataCache.setUsersCurrentBotState(userId, stateOfUser);
            return replyMessageService.getReplyMessage(chatId, processInputNumber(userAnswer, userId, mode));
        }

        else {
            nextState(userId, modeStates);
            replyToUser = replyMessageService.getReplyMessage(userId, questions.get(userDataCache.getUsersCounterState(userId)));
            return replyToUser;
        }
    }

    private void nextState(long userId, List<BotState> modeStates){
        userDataCache.setUsersCurrentBotState(userId, modeStates.get(userDataCache.getUsersCounterState(userId) + 1));
    }

    private boolean isFirstMessageOfCommand(BotState stateOfUser, List<BotState> modeStates) {
        return stateOfUser == modeStates.get(0);
    }

    public void checkAvailability(long userId, BotState stateStart, BotState stateNext){
        if(!userDataCache.getMapOfUserValues().containsKey(userId))
            userDataCache.setUsersListValues(userId, Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0));

        if(userDataCache.getUsersCurrentBotState(userId) == stateStart)
            userDataCache.setUsersCurrentBotState(userId, stateNext);

        if(!userDataCache.getMapOfUsersInfo().containsKey(userId))
            userDataCache.setInfoOfUser(userId, Arrays.asList(0, 0, 0, 0, 0));
    }

    public SendMessage returnMessageOfState(Message message, BotState stateStart, List<BotState> modeStates, List<String> questions, String mode){
        long userId = message.getFrom().getId();

        userDataCache.setUsersModeStates(userId, modeStates);
        checkAvailability(userId, stateStart, modeStates.get(0));
        userDataCache.setUsersCounterState(userId, modeStates.indexOf(userDataCache.getUsersCurrentBotState(userId)));

        if(isLastState(userId, modeStates)){
            return resultMessage(userId, message, mode);
        }

        return filterModeState(message, modeStates, questions, mode);
    }

    /*              9VIP     10VIP    11VIP     12VIP     13VIP    14VIP    15VIP
    *  Fast Rewards: 7         7        7         8         8         8       12    */

    private boolean isBigNumber(String userAnswer){
        boolean isBig = false;

        try{
            isBig = Integer.parseInt(userAnswer) > 500000;
        } catch (Exception e){

        }
        return isBig;
    }

    private boolean isLastState(long userId, List<BotState> modeStates){
        return userDataCache.getUsersCounterState(userId) == modeStates.size() - 1;
    }

    private boolean hasNull(String userAnswer){
        return userAnswer.length() > 1 && userAnswer.charAt(0) == '0';
    }


    private String checkHasException(String userAnswer, long userId, String mode){
        String checkingMessage = "pass";
        int valueOfUser;

        if(mode == "fill info" && userDataCache.getUsersCounterState(userId) >= userDataCache.getUsersModeStates(userId).size() - 2)
            return checkingMessage;

        try{
            valueOfUser = Integer.parseInt(userAnswer);
        } catch (Exception e){
            return getWrongMessage("Invalid number entry, please enter the number again");
        }

        if(isNegativeNumber(userAnswer))
            return getWrongMessage("Negative number, enter positive number");

        if(isBigNumber(userAnswer))
            return getWrongMessage("The number is too large, please enter a number less");

        if(hasNull(userAnswer))
            return getWrongMessage("Invalid number entry, please enter the number again");

        if(mode == "fill info"){
            if(userDataCache.getUsersCounterState(userId) >= userDataCache.getUsersModeStates(userId).size() - 2)
                return checkingMessage;
            else if(userDataCache.getUsersCounterState(userId) == 1 && valueOfUser > 36)
                return getWrongMessage("Invalid VIP level entry");
            else if(userDataCache.getUsersCounterState(userId) > 1 && valueOfUser > 12)
                return getWrongMessage("Invalid number of updates entered");
        }

        return checkingMessage;
    }

    private String getWrongMessage(String errorMessage){
        return errorMessage + " " + Emojis.WRONG_MESSAGE.getEmoji();
    }

    public String processInputNumber(String userAnswer, long userId, String mode){
        boolean hasException = checkHasException(userAnswer, userId, mode) != "pass";

        switch (mode){
            case "fill info":
                if(hasException){
                    return checkHasException(userAnswer, userId, mode);
                }
                else {
                    return countOfLastState(userId, userAnswer, "Invalid message entry\n" +
                            "\n" +
                            "Yes/No");
                }

            default:
                if(hasException)
                    return checkHasException(userAnswer, userId, mode);
                else {
                    userDataCache.getValuesFromUser(userId).set(userDataCache.getIndexOfModeList(userId),
                            Integer.parseInt(userAnswer));
                    return "pass";
                }
        }
    }

    private String countOfLastState(long userId, String userAnswer, String warningMessage){
        int subtractValue = userDataCache.getUsersModeStates(userId).size() - 2;
        boolean isAskingAboutCard = userDataCache.getUsersCounterState(userId) == subtractValue ||
                userDataCache.getUsersCounterState(userId) == subtractValue + 1;

        userAnswer = userAnswer.toLowerCase();
        if (isAskingAboutCard){
            if(userAnswer.equals("yes") || userAnswer.equals("no")) {
                int i = userAnswer.equals("yes")? 1 : 0;
                userDataCache.getListOfUsersInfo(userId).set(userDataCache.getIndexOfModeList(userId), i);
                return "pass";
            }
            else
                return warningMessage;
        }
        else{
            try{
                userDataCache.getListOfUsersInfo(userId).set(userDataCache.getIndexOfModeList(userId), Integer.parseInt(userAnswer));
            } catch (Exception e){
                return "You have entered a letter or character, write number";
            }
            return "pass";
        }
    }

    public SendMessage resultMessage(long userId, Message message, String mode){
        String userAnswer = message.getText();
        String answerToUser = "some message";
        boolean hasException = processInputNumber(userAnswer, userId, mode) != "pass" ? true : false;

        if(hasException){
            userDataCache.setUsersCurrentBotState(userId, userDataCache.getUsersCurrentBotState(userId));
            return replyMessageService.getReplyMessage(message.getChatId(), processInputNumber(userAnswer, userId, mode));
        }
        else {
            answerToUser = modeMessageToUser(mode, userId, message);
            SendMessage replyToUser = replyMessageService.getReplyMessage(message.getChatId(), answerToUser);
            return replyToUser;
        }
    }

    private String getMessageOfUserStatus(long userId){
        int VIP = (int) userDataCache.getListOfUsersInfo(userId).get(0);
        if(VIP < 10)
            return "You have everything ahead little guy " + Emojis.WINK_FACE.getEmoji();
        else if(VIP >= 10 && VIP <= 12)
            return "You are still F2P player " + Emojis.SUNGLASSES.getEmoji();
        else if(VIP > 12 && VIP < 16)
            return "Dolphins are cool too " + Emojis.STAR_STRUCK.getEmoji();
        else
            return "I think Lilith fell in love with you " + Emojis.INNOCENT.getEmoji();
    }

    private void switchToNullState(long userId){
        userDataCache.setUsersCurrentBotState(userId, null);
    }

    private String modeMessageToUser(String mode, long userId, Message message){
        String answerToUser = "ouch...";
        int days = (Integer) userDataCache.getValuesFromUser(userId).get(0);
        switch(mode){

            case "labyrint":
                int current = (int) userDataCache.getValuesFromUser(userId).get(1);
                List<Integer> labRes = eventsDeterminator.determinator(days, LocalDate.now(), "labyrint"
                        , userId, userDataCache.getListOfUser(userId));
                BigInteger result = new BigInteger(String.valueOf(labRes.get(0) + current));
                answerToUser = "In " +
                        eventsDeterminator.getStringFromDate(LocalDate.now().plusDays(days))
                        + " you will have " + result + " labyrinth coins";
                switchToNullState(userId);
                return answerToUser;

            case "po":
                List<Integer> poRes = eventsDeterminator.determinator(days, LocalDate.now(), "PO"
                        , userId, userDataCache.getListOfUser(userId));
                answerToUser = getMessageIncludingCards(userId, poRes, "PO coins", (int) userDataCache.getValuesFromUser(userId).get(1));
                switchToNullState(userId);
                return answerToUser;

            case "bait":
                List<Integer> baitRes = eventsDeterminator.determinator(days, LocalDate.now(), "bait", userId, userDataCache.getListOfUser(userId));
                answerToUser = getMessageIncludingCards(userId, baitRes, " bait", (int) userDataCache.getValuesFromUser(userId).get(1));
                return answerToUser;

            case "twisted":
                List<Integer> TES =  eventsDeterminator.determinator(days, LocalDate.now(), "twisted"
                        , userId, userDataCache.getListOfUser(userId));
                int currentValue = (int) userDataCache.getValuesFromUser(userId).get(1);
                boolean userHasRegularCard = (int) userDataCache.getListOfUsersInfo(userId).get(3) == 1;
                boolean userHasGoldCard = (int) userDataCache.getListOfUsersInfo(userId).get(4) == 1;
                answerToUser = "In " +
                        eventsDeterminator.getStringFromDate(LocalDate.now().plusDays((int) userDataCache.getValuesFromUser(userId).get(0)))
                        + " you will have " + (TES.get(0) + currentValue) + " " + mode + "\nIt equals to " + TES.get(0) / 800 + " branch level ups";
                if(userHasGoldCard && userHasRegularCard) {
                    answerToUser += "\nWith two cards " + (TES.get(3) + currentValue) + " = " + TES.get(3) / 800 + " branch level ups";
                    return answerToUser;
                }
                if(userHasRegularCard)
                    answerToUser += "\nWith regular Card " + (TES.get(1) + currentValue) + " = " + TES.get(1) / 800 + " branch level ups";
                if(userHasGoldCard)
                    answerToUser += "\nWith Gold Card " + (TES.get(2) + currentValue) + " = " + TES.get(2) / 800 + " branch level ups";
                switchToNullState(userId);
                return answerToUser;

            case "essence":
                result = new BigInteger(String.valueOf((((int) userDataCache.getValuesFromUser(userId).get(6) - (int) userDataCache.getValuesFromUser(userId).get(5)) * 44544)));
                int amounthOfHEssence = (((int) userDataCache.getValuesFromUser(userId).get(6) - (int) userDataCache.getValuesFromUser(userId).get(5)) * 44544);
                answerToUser = "For " + userDataCache.getValuesFromUser(userId).get(6) + " level, you need " + result + " essence"
                                + "\n\nYou reach this level " + eventsDeterminator.getStringFromDate(LocalDate.now().plusDays(amounthOfHEssence / (int) userDataCache.getValuesFromUser(userId).get(7)));
                switchToNullState(userId);
                return answerToUser;

            case "fill info":
                userDataCache.setFillingProfileBool(userId, false);
                answerToUser = "Info of " + message.getFrom().getFirstName() + " " + Emojis.FACE_WITH_MONOCLE.getEmoji();
                List<String> info = Arrays.asList("VIP: ", "Quick reward updates: ", "Store updates: ");
                int i = 0;
                while(i < 3) {
                    answerToUser += "\n" + info.get(i) + " " + userDataCache.getListOfUsersInfo(userId).get(i);
                    i++;
                }
                String monthlyCardInfo = (int) userDataCache.getListOfUsersInfo(userId).get(3) == 1 ? "Monthly card active" : "Monthly card is inactive";
                String goldMonthlyCardInfo = (int) userDataCache.getListOfUsersInfo(userId).get(4) == 1 ? "Gold Monthly card active" : "Gold Monthly card is inactive";
                answerToUser += "\n" + monthlyCardInfo + "\n" + goldMonthlyCardInfo + "\n\n" + getMessageOfUserStatus(userId) + "\n\nIf you have entered incorrect information about your profile, use the /changeprofile command to change the information";
                switchToNullState(userId);
                return answerToUser;
            default:
                return answerToUser;
        }
    }

    private String getMessageIncludingCards(long userId, List<Integer> resources, String mode, int currentValue){
        boolean userHasRegularCard = (int) userDataCache.getListOfUsersInfo(userId).get(3) == 1;
        boolean userHasGoldCard = (int) userDataCache.getListOfUsersInfo(userId).get(4) == 1;
        String text = "In " +
                eventsDeterminator.getStringFromDate(LocalDate.now().plusDays((int) userDataCache.getValuesFromUser(userId).get(0)))
                + " you will have " + (resources.get(0) + currentValue) + " " + mode;
        if(userHasGoldCard && userHasRegularCard) {
            text += "\nWith two cards " + (resources.get(3) + currentValue);
            return text;
        }
        if(userHasRegularCard)
            text += "\nWith regular Card " + (resources.get(1) + currentValue);
        if(userHasGoldCard)
            text += "\nWith Gold Card " + (resources.get(2) + currentValue);
        return text;
    }

    private boolean isNegativeNumber(String answer){
        return answer.charAt(0) == '-';
    }


}
