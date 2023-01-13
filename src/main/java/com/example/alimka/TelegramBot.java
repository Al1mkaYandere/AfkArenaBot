package com.example.alimka;

import com.example.alimka.BotApi.TelegramFacade;
import com.example.alimka.appconfig.BotConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TelegramBot extends TelegramWebhookBot {
    BotConfig config;
    private TelegramFacade telegramFacade;

    @Autowired
    public TelegramBot(BotConfig config, TelegramFacade telegramFacade){
        this.config = config;
        this.telegramFacade = telegramFacade;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        SendMessage replyMessageToUser = telegramFacade.handleUpdate(update);

        return replyMessageToUser;
    }

    @Override
    public String getBotUsername() {
        return config.getUserName();
    }

    @Override
    public String getBotToken() {
        return config.getBotToken();
    }

    @Override
    public String getBotPath() {
        return config.getWebHookPath();
    }

}
