package com.example.alimka.Cache;

import com.example.alimka.BotApi.BotState;
import com.example.alimka.BotApi.util.Emojis;
import com.example.alimka.Service.ReplyMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.*;

@Service
public class UserDataCache implements DataCache{
    @Autowired
    private ReplyMessageService replyMessageService;
    private HashMap<Long, List> modeOfUser = new HashMap<>();
    private HashMap<Long, List> usersValues = new HashMap<>();
    private HashMap<Long, BotState> usersBotStates = new HashMap<>();
    private HashMap<Long, Integer> usersCounterState = new HashMap<>();
    private HashMap<Long, List> usersInfo = new HashMap<>();
    private HashMap<Long, List> usersModeStates = new HashMap<>();
    private HashMap<Long, Boolean> usersFillMap = new HashMap<>();

    public void setUsersListValues(long userId, List<Integer> values){
        usersValues.put(userId, values);
    }

    public SendMessage processFillingProfile(Message message){
        long userId = message.getFrom().getId();
        usersInfo.put(userId, Arrays.asList(0, 0, 0, 0, 0));
        setUsersCurrentBotState(userId, BotState.FILLING_USER_REFRESH_ASK);
        return replyMessageService.getReplyMessage(message.getChatId(), "To get started, you must complete your profile " + Emojis.SPACE_INVADER.getEmoji() + "\nYour VIP status level");
    }

    public HashMap getMapOfFillingInfo(){
        return usersFillMap;
    }

    public boolean getInfoFillingProfile(long userId){
        return usersFillMap.get(userId);
    }

    public void setFillingProfileBool(long userId, boolean info){
        usersFillMap.put(userId, info);
    }

    public void setUsersModeStates(long userId, List<BotState> modeStates){
        usersModeStates.put(userId, modeStates);
    }

    public List getUsersModeStates(long userId){
        return usersModeStates.get(userId);
    }

    public List getListOfUsersInfo(long userId){
        return usersInfo.get(userId);
    }

    public void setInfoOfUser(long userId, List<Integer> infoList){
        usersInfo.put(userId, infoList);
    }

    public HashMap<Long, List> getMapOfUsersInfo(){return usersInfo;}

    public List<Integer> getListOfUser(long userId){
        return usersValues.get(userId);
    }

    public HashMap<Long, List> getMapOfUserValues(){
        return usersValues;
    }

    public List getValuesFromUser(long userId){
        return usersValues.get(userId);
    }

    public void setModeOfUser(long userId, List<Integer> modeList){
        modeOfUser.put(userId, modeList);
    }

    public int getIndexOfModeList(long userId){
        return (int) modeOfUser.get(userId).get(usersCounterState.get(userId) - 1);
    }

    @Override
    public void setUsersCurrentBotState(long userId, BotState botState) {
        usersBotStates.put(userId, botState);
    }

    @Override
    public BotState getUsersCurrentBotState(long userId) {
        BotState botState = usersBotStates.get(userId);
        return botState;
    }

    public void setUsersCounterState(long userId, int counter){
        usersCounterState.put(userId, counter);
    }

    public int getUsersCounterState(long userId){
        return usersCounterState.get(userId);
    }



}
