package com.example.alimka.BotApi.States.Modes;


import com.example.alimka.BotApi.BotState;
import com.example.alimka.BotApi.InputMessageHandler;
import com.example.alimka.BotApi.util.FilterOfStates;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.*;

@Service
@Slf4j
@Component
public class PO implements InputMessageHandler {
    @Autowired
    private FilterOfStates filterOfStates;
    private List<BotState> poStates = Arrays.asList(BotState.PO_DAYS_ASK, BotState.PO_CURRENT, BotState.PO_IN_BOSS, BotState.PO_RESULT);
    List<String> poQuestions = Arrays.asList("How many days do you want to know the result?", "Current amount of PO coins", "How much do you claim PO from Twisted realm");

    @Override
    public SendMessage handle(Message message) {
        return filterOfStates.returnMessageOfState(message, BotState.PO_COINS, poStates, poQuestions, "po");
    }

    @Override
    public boolean accept(BotState botState) {
        return botState == BotState.PO_COINS || botState ==  BotState.PO_CURRENT
                || botState == BotState.PO_IN_BOSS
                || botState ==  BotState.PO_DAYS_ASK
                || botState == BotState.PO_RESULT;
    }
}
