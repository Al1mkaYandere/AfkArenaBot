package com.example.alimka.BotApi;

import com.example.alimka.Cache.UserDataCache;
import com.example.alimka.Service.ReplyMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
@Component
public class TelegramFacade {
    private UserDataCache userDataCache;
    private BotStateContext botStateContext;
    private ReplyMessageService replyMessageService;

    @Autowired
    public TelegramFacade(UserDataCache userDataCache, BotStateContext botStateContext, ReplyMessageService replyMessageService) {
        this.userDataCache = userDataCache;
        this.botStateContext = botStateContext;
        this.replyMessageService = replyMessageService;
    }

    public SendMessage handleUpdate(Update update) {
        SendMessage replyMessage = null;

        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from User:{}, chatId: {},  with text: {}",
                    message.getFrom().getUserName(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }

        return replyMessage;
    }

    private SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        long userId = message.getFrom().getId();
        BotState botState;
        SendMessage replyMessage;

        switch (inputMsg) {
            case "/start":
                botState = BotState.START;
                break;
            case "/level":
                botState = BotState.ESSENCE;
                break;
            case "/po":
                botState = BotState.PO_COINS;
                break;
            case "/tournament":
                botState = BotState.TOURNAMENT_MONEYS;
                break;
            case "/twisted":
                botState = BotState.TESSENCE;
                break;
            case "/changeprofile":
                botState = BotState.FILLING_USER_INFO;
                break;
            case "/labyrinth":
                botState = BotState.LAB_DAYS_ASK;
                break;
            case "/menu":
                botState = BotState.MAIN_MENU;
                break;
            case "/description":
                botState = BotState.DESCRIPTION;
                break;
            case "/bait":
                botState = BotState.BAIT_AMOUNT;
                break;
            case "/events":
                botState = BotState.NEARLY_EVENTS;
                break;
            default:
                botState = userDataCache.getUsersCurrentBotState(userId);
                break;
        }

        if(didntFill(userId, botState) && isNotStartOrDescriptionState(botState)) {
            if(!userDataCache.getMapOfFillingInfo().containsKey(userId))
                userDataCache.setFillingProfileBool(userId, true);

            return userDataCache.processFillingProfile(message);
        }


        userDataCache.setUsersCurrentBotState(userId, botState);

        replyMessage = botStateContext.processInputMessage(botState, message);

        return replyMessage;
    }

    private boolean isNotStartOrDescriptionState(BotState botState){
        return botState != BotState.START && botState != BotState.DESCRIPTION && botState != BotState.FILLING_USER_INFO;
    }

    private boolean didntFill(long userId, BotState botState){
        List<BotState> fillingProfileStates = Arrays.asList(BotState.FILLING_USER_INFO,
                BotState.FILLING_USER_REFRESH_ASK,
                BotState.FILLING_USER_MONTHLYCARD_ASK,
                BotState.FILLING_USER_SHOPREFRESH_ASK,
                BotState.FILLING_USER_VIP_ASK,
                BotState.FILLING_USER_GOLDMONTHLYCARD_ASK,
                BotState.FILLING_USER_RESULT);
        if(!userDataCache.getMapOfFillingInfo().containsKey(userId))
            userDataCache.setFillingProfileBool(userId, true);

        for(int i = 0; i < 7; i++){
            if(botState == fillingProfileStates.get(i))
                return false;
        }

        return userDataCache.getInfoFillingProfile(userId);
    }

}
