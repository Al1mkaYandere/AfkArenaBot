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
public class Description implements InputMessageHandler {
    private UserDataCache userDataCache;
    private ReplyMessageService replyMessageService;

    public Description(UserDataCache userDataCache, ReplyMessageService replyMessageService){
        this.userDataCache = userDataCache;
        this.replyMessageService = replyMessageService;
    }
    @Override
    public SendMessage handle(Message message) {
        long chatId = message.getChatId();
        long userId = message.getFrom().getId();
        String text = EmojiParser.parseToUnicode("This is a beta version of the bot " + Emojis.EXPLODING_HEAD.getEmoji() +", " +
                "in the future commands will be added to the bot " + Emojis.PARTY_GUY.getEmoji() +".\n" +
                "\n" +
                "If your VIP level is less than 10 and your hero level is less than 450, \n" +
                "the bot will not be able to calculate the number of days it takes to reach a certain level. " +
                "There will also be an incorrect calculation of PO coins.\n\n" +
                "The calculation includes the condition when you go through the difficult mode of the labyrinth." + "\n\nWhen using the bot, the bot takes into account the fact that you have not collected rewards for any mode today.\n" +
                "\n" +
                "When using the /level command, you should know how much essence you collect per day, you cannot calculate it in 1 one day, you need regular statistics, or see how many levels you raise in one month and divide by 44544, this will be your amount per day."
                + "\n\nThe bot does not take into account the Cursed realm");
        SendMessage replyToUser = replyMessageService.getReplyMessage(chatId, text);
        userDataCache.setUsersCurrentBotState(userId, null);
        return replyToUser;
    }

    @Override
    public boolean accept(BotState botState) {
        return botState == BotState.DESCRIPTION;
    }
}
