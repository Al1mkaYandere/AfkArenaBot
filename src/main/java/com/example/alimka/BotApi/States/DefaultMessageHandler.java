package com.example.alimka.BotApi.States;

import com.example.alimka.BotApi.BotState;
import com.example.alimka.BotApi.InputMessageHandler;
import com.example.alimka.BotApi.util.Emojis;
import com.example.alimka.Service.ReplyMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class DefaultMessageHandler implements InputMessageHandler{
    @Autowired
    private ReplyMessageService replyMessageService;
    @Override
    public SendMessage handle(Message message) {
        long chatId = message.getChatId();
        String messageOfUser = message.getText();
        SendMessage replyToUser = replyMessageService.getReplyMessage(chatId,
                "Unknown command " + '"' + messageOfUser + '"' + " " + Emojis.WRONG_MESSAGE.getEmoji() +
                           "\n\nYou can use these commands for calculating:\n\n" +
                        "/labyrinth - the number of labyrinth coins\n\n" +
                        "/twisted - the number of twisted essence (does not take into account the cursed realm)\n\n" +
                        "/po - the number of PO coins\n\n" +
                        "/tournament - the number of tournament coins\n\n" +
                        "/level - find out the required amount of time to reach the level\n\n" +
                        "/bait - find the amount of bait\n\n")  ;
        return replyToUser;
    }

    @Override
    public boolean accept(BotState botState) {
        return false;
    }

}
