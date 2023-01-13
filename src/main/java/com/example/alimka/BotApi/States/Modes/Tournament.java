package com.example.alimka.BotApi.States.Modes;


import com.example.alimka.BotApi.BotState;
import com.example.alimka.BotApi.InputMessageHandler;
import com.example.alimka.Cache.UserDataCache;
import com.example.alimka.Service.ReplyMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Component
public class Tournament implements InputMessageHandler {
    private UserDataCache userDataCache;
    private ReplyMessageService replyMessageService;
    private Map<Long, List> Users = new LinkedHashMap<>();

    public Tournament(UserDataCache userDataCache, ReplyMessageService replyMessageService){
        this.userDataCache = userDataCache;
        this.replyMessageService = replyMessageService;
    }

    @Override
    public SendMessage handle(Message message) {
        if(userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.TOURNAMENT_MONEYS))
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.TOURNAMENT_CURRENT);



        return processUsersInput(message);
    }

    @Override
    public boolean accept(BotState botState) {
        return botState == BotState.TOURNAMENT_MONEYS || botState == BotState.TOURNAMENT_CURRENT
                || botState ==  BotState.TOURNAMENT_ASK_MONEY_PERH
                || botState ==  BotState.TOURNAMENT_DAYS_ASK
                || botState == BotState.TOURNAMENT_RESULT;
    }

    public SendMessage processUsersInput(Message message){
        String userAnswer = message.getText();
        long chatId = message.getChatId();
        long userId = message.getFrom().getId();

        BotState botState = userDataCache.getUsersCurrentBotState(userId);

        if (!Users.containsKey(userId)) {
            Users.put(userId, new ArrayList());
        }

        SendMessage replyToUser = null;
        if (botState.equals(BotState.TOURNAMENT_CURRENT)){
            replyToUser = replyMessageService.getReplyMessage(chatId, "Enter your current number of coins");
            userDataCache.setUsersCurrentBotState(userId, BotState.TOURNAMENT_ASK_MONEY_PERH);
        }

        if (botState.equals(BotState.TOURNAMENT_ASK_MONEY_PERH)){
            try{
                Users.get(userId).add(Integer.parseInt(userAnswer)); // - 0 current
            } catch (Exception e){
                replyToUser = replyMessageService.getReplyMessage(chatId, "You have entered a letter or character, write number");
                userDataCache.setUsersCurrentBotState(userId, BotState.TOURNAMENT_ASK_MONEY_PERH);
                return replyToUser;
            }
            replyToUser = replyMessageService.getReplyMessage(chatId, "How much do you claim money per hour");
            userDataCache.setUsersCurrentBotState(userId, BotState.TOURNAMENT_DAYS_ASK);
        }

        if(botState.equals(BotState.TOURNAMENT_DAYS_ASK)){
            try{
                Users.get(userId).add(Integer.parseInt(userAnswer)); // - 1 money per hour
            } catch (Exception e){
                replyToUser = replyMessageService.getReplyMessage(chatId, "You have entered a letter or character, write number");
                userDataCache.setUsersCurrentBotState(userId, BotState.TOURNAMENT_DAYS_ASK);
                return replyToUser;
            }
            replyToUser = replyMessageService.getReplyMessage(chatId, "Enter count of days");
            userDataCache.setUsersCurrentBotState(userId, BotState.TOURNAMENT_RESULT);
        }

        if (botState.equals(BotState.TOURNAMENT_RESULT)){
            try{
                Users.get(userId).add(Integer.parseInt(userAnswer)); // - 2 days
            } catch (Exception e){
                replyToUser = replyMessageService.getReplyMessage(chatId, "You have entered a letter or character, write number");
                userDataCache.setUsersCurrentBotState(userId, BotState.TOURNAMENT_RESULT);
                return replyToUser;
            }
            int tournament = (int) Users.get(userId).get(0) + ((int)Users.get(userId).get(1) * (int)Users.get(userId).get(2) * 24);
            BigInteger result = new BigInteger(String.valueOf(tournament));
            int month = (int)Users.get(userId).get(2) / 30;
            String answerToUser = month == 0 ?
                    "After " + Users.get(userId).get(2) + " days, you will claim " + result + " tournament coins"
                    :
                    "After " + month + " month and " + ((int) Users.get(userId).get(2) % 30) + " days, you will have " + result + " tournament coins";
            replyToUser = replyMessageService.getReplyMessage(chatId, answerToUser);
            Users.get(userId).clear();
        }

        return replyToUser;
    }
}
