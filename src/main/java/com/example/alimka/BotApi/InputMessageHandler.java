package com.example.alimka.BotApi;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface InputMessageHandler {
    SendMessage handle(Message message);

    boolean accept(BotState botState);
}
