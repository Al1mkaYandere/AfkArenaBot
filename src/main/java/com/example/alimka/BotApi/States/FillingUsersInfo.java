package com.example.alimka.BotApi.States;

import com.example.alimka.BotApi.BotState;
import com.example.alimka.BotApi.InputMessageHandler;
import com.example.alimka.BotApi.util.FilterOfStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Arrays;
import java.util.List;

@Component
public class FillingUsersInfo implements InputMessageHandler {
    @Autowired
    private FilterOfStates filterOfStates;
    private List<BotState> fillingUsersStates = Arrays.asList(BotState.FILLING_USER_VIP_ASK, BotState.FILLING_USER_REFRESH_ASK, BotState.FILLING_USER_SHOPREFRESH_ASK, BotState.FILLING_USER_MONTHLYCARD_ASK, BotState.FILLING_USER_GOLDMONTHLYCARD_ASK, BotState.FILLING_USER_RESULT);
    private List<String> fillingUsersQuestions = Arrays.asList("Your VIP status level", "Number of quick reward updates", "Number of store updates", "Do you have regular monthly card\nYes or No", "Do you have gold monthly card\nYes or No");
    @Override
    public SendMessage handle(Message message) {
        return filterOfStates.returnMessageOfState(message, BotState.FILLING_USER_INFO, fillingUsersStates, fillingUsersQuestions, "fill info");
    }

    @Override
    public boolean accept(BotState botState) {
        return botState == BotState.FILLING_USER_INFO
                || botState == BotState.FILLING_USER_VIP_ASK
                || botState == BotState.FILLING_USER_REFRESH_ASK
                || botState == BotState.FILLING_USER_SHOPREFRESH_ASK
                || botState == BotState.FILLING_USER_MONTHLYCARD_ASK
                || botState == BotState.FILLING_USER_GOLDMONTHLYCARD_ASK
                || botState == BotState.FILLING_USER_RESULT;
    }
}
