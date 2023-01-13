package com.example.alimka.BotApi.States.Modes;

import com.example.alimka.BotApi.BotState;
import com.example.alimka.BotApi.InputMessageHandler;
import com.example.alimka.BotApi.util.FilterOfStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.*;

@Component
public class Bait implements InputMessageHandler {
    @Autowired
    private FilterOfStates filterOfStates;
    private List<BotState> baitStates = Arrays.asList(BotState.BAIT_DAYS_ASK, BotState.BAIT_CURRENT, BotState.BAIT_RESULT);
    List<String> baitQuestions = Arrays.asList("How many days do you want to know the result?", "How much bait now?");

    @Override
    public SendMessage handle(Message message) {
        return filterOfStates.returnMessageOfState(message, BotState.BAIT_AMOUNT,baitStates, baitQuestions, "bait");
    }

    @Override
    public boolean accept(BotState botState) {
        return botState == BotState.BAIT_AMOUNT || botState ==  BotState.BAIT_CURRENT
                || botState ==  BotState.BAIT_DAYS_ASK
                || botState == BotState.BAIT_RESULT;
    }

}
