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
public class Labyrinth implements InputMessageHandler {
    @Autowired
    private FilterOfStates filterOfStates;
    private List<BotState> labStates = Arrays.asList(BotState.LAB_DAYS_ASK, BotState.LAB_CURRENT, BotState.LAB_RESULT);
    List<String> labQuestions = Arrays.asList("How many days do you want to know the result?", "Current amount of labyrinth coins");

    @Override
    public SendMessage handle(Message message) {
        return filterOfStates.returnMessageOfState(message, BotState.LAB_COINS,labStates, labQuestions, "labyrint");
    }

    @Override
    public boolean accept(BotState botState) {
        return botState == BotState.LAB_COINS || botState ==  BotState.LAB_CURRENT
                || botState ==  BotState.LAB_DAYS_ASK
                || botState == BotState.LAB_RESULT;
    }
}

