package com.example.alimka.Service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
@Service
public class ReplyMessageService {
    public SendMessage getReplyMessage(long chatId, String replyMessage){
        return new SendMessage(Long.toString(chatId), /*localeMessageService.getMessage(replyMessage)*/ replyMessage);
    }

//    public SendMessage getReplyMessage(long chatId, String replyMessage, Object... args){
//        return new SendMessage(Long.toString(chatId), /*localeMessageService.getMessage(replyMessage, args)*/ replyMessage);
//    }
}
