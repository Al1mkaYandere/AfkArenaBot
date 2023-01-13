package com.example.alimka.BotApi;

import com.example.alimka.BotApi.States.DefaultMessageHandler;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BotStateContext {
    private List<InputMessageHandler> messageHandlers;

    private DefaultMessageHandler defaultHandler;

    public BotStateContext(List<InputMessageHandler> messageHandlers, DefaultMessageHandler defaultHandler){
       this.messageHandlers = messageHandlers;
       this.defaultHandler = defaultHandler;
    }

    public SendMessage processInputMessage(BotState state, Message message) {
        InputMessageHandler currentMessageHandler = messageHandlers.stream()
                .filter(h->h.accept(state))
                .findFirst()
                .orElse(defaultHandler);

        System.out.println("State - " + state);

        return currentMessageHandler.handle(message);
    }


}
