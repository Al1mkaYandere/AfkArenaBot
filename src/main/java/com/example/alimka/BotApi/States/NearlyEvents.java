package com.example.alimka.BotApi.States;

import com.example.alimka.BotApi.BotState;
import com.example.alimka.BotApi.InputMessageHandler;
import com.example.alimka.BotApi.util.EventsDeterminator;
import com.example.alimka.BotApi.util.FilterOfStates;
import com.example.alimka.Cache.UserDataCache;
import com.example.alimka.Service.ReplyMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;
import java.util.*;

@Component
public class NearlyEvents implements InputMessageHandler {
    private EventsDeterminator eventsDeterminator;
    private ReplyMessageService replyMessageService;

    public NearlyEvents(EventsDeterminator eventsDeterminator, ReplyMessageService replyMessageService) {
        this.eventsDeterminator = eventsDeterminator;
        this.replyMessageService = replyMessageService;
    }

    @Override
    public SendMessage handle(Message message) {
        return replyMessageService.getReplyMessage(message.getChatId(), eventsDeterminator.getNearlyEvents(LocalDate.now()));
    }

    @Override
    public boolean accept(BotState botState) {
        return botState == BotState.NEARLY_EVENTS;
    }

}