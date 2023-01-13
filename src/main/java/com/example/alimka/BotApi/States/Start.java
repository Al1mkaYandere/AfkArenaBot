package com.example.alimka.BotApi.States;

import com.example.alimka.BotApi.BotState;
import com.example.alimka.BotApi.InputMessageHandler;
import com.example.alimka.BotApi.util.Emojis;
import com.example.alimka.Cache.UserDataCache;
import com.example.alimka.Service.ReplyMessageService;
import com.vdurmont.emoji.EmojiParser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
@Component
public class Start implements InputMessageHandler {
    private UserDataCache userDataCache;
    private ReplyMessageService replyMessageService;

    public Start(UserDataCache userDataCache, ReplyMessageService replyMessageService){
        this.userDataCache = userDataCache;
        this.replyMessageService = replyMessageService;
    }
    @Override
    public SendMessage handle(Message message) {
        long chatId = message.getChatId();
        long userId = message.getFrom().getId();
        String username = message.getFrom().getFirstName();
        String text = EmojiParser.parseToUnicode("Hi, " + Emojis.HI.getEmoji() + " " + username + "\n" +
                "With this bot you can find out how many resources you will have after a certain time.\n\n" +
                "The bot takes into account double reward events.\n\n" +
                "You can use these commands for calculating:\n\n" +
                "/labyrinth - the number of labyrinth coins\n\n" +
                "/twisted - the number of twisted essence (does not take into account the cursed realm)\n\n" +
                "/po - the number of PO coins\n\n" +
                "/tournament - the number of tournament coins\n\n" +
                "/level - find out the required amount of time to reach the level\n\n" +
                "/bait - find the amount of bait\n\n" +
                "Important information about bot " + Emojis.COMPUTER.getEmoji() + " /description ");
        SendMessage replyToUser = replyMessageService.getReplyMessage(chatId, text);
        userDataCache.setUsersCurrentBotState(userId, null);
        return replyToUser;
    }

    @Override
    public boolean accept(BotState botState) {
        return botState == BotState.START;
    }

}
