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
public class Level implements InputMessageHandler {
    @Autowired
    private FilterOfStates filterOfStates;
    private List<BotState> essenceStates = Arrays.asList(BotState.ESSENCE_ASK_YLEVEL, BotState.ESSENCE_ASK_RLEVEL, BotState.ESSENCE_PERDAY_ASK, BotState.ESSENCE_RESULT);
    List<String> essenceQuestions = Arrays.asList("What level are you?", "What level do you want to reach", "How much do you collect hero`s essence per day");

    @Override
    public SendMessage handle(Message message) {
        return filterOfStates.returnMessageOfState(message, BotState.ESSENCE, essenceStates, essenceQuestions, "essence");
    }

    @Override
    public boolean accept(BotState botState) {
        return botState == BotState.ESSENCE || botState ==  BotState.ESSENCE_ASK_YLEVEL
                || botState ==  BotState.ESSENCE_ASK_RLEVEL
                || botState == BotState.ESSENCE_PERDAY_ASK
                || botState == BotState.ESSENCE_RESULT;
    }
}
