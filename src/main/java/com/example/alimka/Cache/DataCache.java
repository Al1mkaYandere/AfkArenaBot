package com.example.alimka.Cache;

import com.example.alimka.BotApi.BotState;
import org.springframework.stereotype.Component;

@Component
public interface DataCache {

    void setUsersCurrentBotState(long userId, BotState botState);

    BotState getUsersCurrentBotState(long userId);

//    UserProfileData getUserProfileData(int userId);

//    void saveUserProfileData(int userId, UserProfileData userProfileData);
}
