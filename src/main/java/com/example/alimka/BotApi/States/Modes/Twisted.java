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
public class Twisted implements InputMessageHandler {
    @Autowired
    private FilterOfStates filterOfStates;
    private List<BotState> twistedStates = Arrays.asList(BotState.TESSENCE_DAYS_ASK, BotState.TESSENCE_CURRENT_ASK, BotState.TESSENCE_COUNT_ASK, BotState.TESSENCE_RESULT);
    List<String> twistedQuestions = Arrays.asList("How many days do you want to know the result?", "Current amounth of Twisted Essence", "How much do you claim Twisted essence from Twisted Realm  ?");

    @Override
    public SendMessage handle(Message message) {
        return filterOfStates.returnMessageOfState(message, BotState.TESSENCE, twistedStates, twistedQuestions, "twisted");    }

    @Override
    public boolean accept(BotState botState) {
        return botState == BotState.TESSENCE
                || botState == BotState.TESSENCE_CURRENT_ASK
                || botState ==  BotState.TESSENCE_COUNT_ASK
                || botState ==  BotState.TESSENCE_DAYS_ASK
                || botState ==  BotState.TESSENCE_RESULT;
    }
}
