package com.example.alimka.BotApi.util;

import com.example.alimka.Cache.UserDataCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
@Component
public class EventsDeterminator {
    /*Функция отвечает за то чтобы ввести новую дату для ивента*/
    @Autowired
    private UserDataCache userDataCache;

    public LocalDate returningNewEventDate(LocalDate usersDate, LocalDate eventDate){
        int differenceInDays = (int) ChronoUnit.DAYS.between(eventDate, usersDate);
        if (differenceInDays <= 24 && differenceInDays >= 8)
            eventDate = eventDate.plusDays(24);
        while(differenceInDays >= 24){
            eventDate = eventDate.plusDays(24);
            differenceInDays -= 24;
        }

        return eventDate;
    }

    /* Функция проверяющая находится ли юзер на втором дне события из двух*/
    public boolean isSecondDay(LocalDate usersDate){
        LocalDate everyEventDate = LocalDate.of(2022, 12, 26);
        return ChronoUnit.DAYS.between(everyEventDate, usersDate) % 2 != 0;
    }

    public List<Integer> determinator(int days, LocalDate usersDate, String mode, long userId, List<Integer> resources){
        LocalDate eventDate = getEventDate(mode);
        eventDate = returningNewEventDate(usersDate, eventDate);
        int eventDays = getDaysFromEvent(eventDate, usersDate, days);
        /* Если пользователь на втором дне то он прибавляет день
        *  так как идет учет того что он именно в этот день будет играть ивент*/
        if(isSecondDay(usersDate))
            days++;
        return wish(days, eventDays, mode, userId, resources);
    }

    public int getDaysFromEvent(LocalDate eventDate, LocalDate usersDate, int days) {
        int daysInEvent = 0;
        int fromUserDateToEventDate = (int) ChronoUnit.DAYS.between(usersDate, eventDate.plusDays(7));
        for (int i = 0; i <= days; i++) {
            if (fromUserDateToEventDate <= 7 && fromUserDateToEventDate >= 0)
                daysInEvent++;
            else if (fromUserDateToEventDate < 0) {
                eventDate = eventDate.plusDays(24);
            }
            usersDate = usersDate.plusDays(1);
            fromUserDateToEventDate = (int) ChronoUnit.DAYS.between(usersDate, eventDate.plusDays(7));
        }
        return daysInEvent;
    }

    private LocalDate getEventDate(String mode){
        switch (mode){
            case "labyrint":
                return LocalDate.of(2022, 12, 10);
            case "tournament":
                return LocalDate.of(2022, 12, 10);
            case "twisted":
                return LocalDate.of(2022, 12, 26);
            case "PO":
                return LocalDate.of(2022, 12, 26);
            case "essence":
                return LocalDate.of(2023, 12, 18);
            case "bait":
                return LocalDate.of(2022, 12, 10);
            default:
                return LocalDate.now();
        }
    }

    private List<Integer> resultLabyrint(int days, int eventDays, long userId){
        int lvlVIP = (int) userDataCache.getListOfUsersInfo(userId).get(0);
        int labCoinsForOneSkip = lvlVIP < 14 ? 6620 : 13240;
        int result = (labCoinsForOneSkip * Math.round(days / 2f)) + (labCoinsForOneSkip * Math.round(eventDays / 2f));
        return Arrays.asList(result);
    }

    private int getInfoAboutGameFromUser(long userId, String wishOfTaking){
        int value = 0;
        List<String> list = Arrays.asList("VIP", "QuickRefreshes", "StoreRefreshes", "RegularCard", "GoldCard");
        for(int i = 0; i < 5; i++){
            if(wishOfTaking == list.get(i))
                value = (int) userDataCache.getListOfUsersInfo(userId).get(list.indexOf(wishOfTaking));
        }
        return value;
    }

    private List<Integer> returnListOfResWithCards(long userId, int resourcesWithoutCard, int resourcesWithRegularCard, int resourcesWithGoldCard, int days){
        boolean userHasRegularCard = getInfoAboutGameFromUser(userId, "RegularCard") == 1;
        boolean userHasGoldCard = getInfoAboutGameFromUser(userId, "GoldCard") == 1;
        List<Integer> resources = Arrays.asList(0, 0, 0, 0);
        resources.set(0, resourcesWithoutCard);
        if(userHasRegularCard)
            resources.set(1, resourcesWithoutCard + (resourcesWithRegularCard * days));
        if(userHasGoldCard)
            resources.set(2, resourcesWithoutCard + (resourcesWithGoldCard * days));
        if(userHasGoldCard && userHasRegularCard)
            resources.set(3, resourcesWithoutCard + ((resourcesWithGoldCard + resourcesWithRegularCard) * days));

        return resources;
    }

    private List<Integer> resultTwisted(int days, int eventDays, int tesFromOneBoss, long userId){
        int countOfQuickRefreshes = getInfoAboutGameFromUser(userId, "QuickRefreshes");
        int TESQ = Math.round(countOfQuickRefreshes * days * 2.5f);
        int TESfromAfk = 33 * (days - 1);
        int resourcesWithGoldCard = getInfoAboutGameFromUser(userId, "VIP") >= 12 ? 80 : 40;
        int resultWithoutCards = TESfromAfk + TESQ + (tesFromOneBoss * Math.round(days / 2f)) + (tesFromOneBoss * Math.round(eventDays / 2f));
        List<Integer> resources = returnListOfResWithCards(userId, resultWithoutCards, 20, resourcesWithGoldCard, days);
        return resources;
    }

    private int resultTournament(int days, int i, int difference){
        return 2;
    }

    private int getUserCards(long userId){
        int cardCount = 0;
        if(getInfoAboutGameFromUser(userId, "RegularCard") == 1)
            cardCount++;
        if(getInfoAboutGameFromUser(userId, "GoldCard") == 1)
            cardCount++;
        return cardCount;
    }

    public String getNearlyEvents(LocalDate dateOfUser){
        String text = "";
        List<LocalDate> datesOfEvents = Arrays.asList(
                getEventDate("labyrint"),
                getEventDate("twisted"),
                getEventDate("essence"));
        boolean isBeforeDateOfUser = true;
//        while(isBeforeDateOfUser){ /* 2022 12 10   -      2023 01 03    2023 01 07*/
//            for(int i = 0; i < 3; i++){
//                if(ChronoUnit.DAYS.between(datesOfEvents.get(i).plusDays(24), dateOfUser) < )
//            }
//        }
        List<String> namesOfEvents = Arrays.asList("\"Labyrinth Limbo\"", "\"Twisted Realm\"", "\"Bountiful Bounties\"");

        for(int i = 0; i < 3; i++){
//            if(ChronoUnit.DAYS.between(dateOfUser, datesOfEvents.get(i).plusDays(7)) > 0){
//                text = "The " + namesOfEvents.get(i) + " event is currently underway";
//            }
            text += datesOfEvents.get(i) + " " + namesOfEvents.get(i) + "\n\n";
        }

        return text;
    }
    private List<Integer> resultPo(int days, int eventDays, int PO, long userId){
        int countOfStoreRefreshes = getInfoAboutGameFromUser(userId, "StoreRefreshes");
        int countOfQuickRewardsRefreshes = getInfoAboutGameFromUser(userId, "QuickRefreshes");
        int poFromStore = (250 + (countOfStoreRefreshes * 250)) * days;
        int poFromQuickRewards = Math.round(countOfQuickRewardsRefreshes * 45.5f) * days;
        int poFromDefaultTwistedRealm = PO  * Math.round(days / 2f);
        int poFromDoubleTwistedRealm = PO * Math.round(eventDays / 2f);
        int poFromAfk = 550 * (days - 1);
        int resourcesWithGoldCard = getInfoAboutGameFromUser(userId, "VIP") >= 12 ? 800 : 400;
        int resultWithoutCards = poFromAfk + poFromStore + poFromQuickRewards + poFromDefaultTwistedRealm + poFromDoubleTwistedRealm;
        List<Integer> resources = returnListOfResWithCards(userId, resultWithoutCards, 200, resourcesWithGoldCard, days);
        return resources;
    }

    private List<Integer> resultBait(int days, int eventDays, long userId){
        System.out.println(eventDays);
        int countOfQuickRewardsRefreshes = getInfoAboutGameFromUser(userId, "QuickRefreshes");
        int baitWithoutCards = (20 * (days - 1)) + (6 * Math.round(days / 2f)) + (6 * Math.round(eventDays / 2f)) + (Math.round(countOfQuickRewardsRefreshes * 1.5f) * days);
        int resourcesWithGoldCard = getInfoAboutGameFromUser(userId, "GoldCard") == 1 ? 10 : 0;
        List<Integer> resources = returnListOfResWithCards(userId, baitWithoutCards, 0, resourcesWithGoldCard, days);
        return resources;
    }

    private List<Integer> wish(int days, int eventDays, String mode, long userId, List<Integer> resources){
        switch (mode){
            case "labyrint":
                return resultLabyrint(days, eventDays, userId);
            case "twisted":
                return resultTwisted(days, eventDays, resources.get(2), userId);
            case "PO":
                return resultPo(days, eventDays, resources.get(4), userId);
            case "bait":
                return resultBait(days, eventDays, userId);
            default:
                return Arrays.asList(0, 0);
        }
    }

    private int getDay(LocalDate date){
        String dates = String.valueOf(date);
        String[] datesToSplit = dates.split("-");
        String day = datesToSplit[2].charAt(0) == '0' ? String.valueOf(datesToSplit[2].charAt(1)) : datesToSplit[2];
        int dayNum = Integer.parseInt(day);
        return dayNum;
    }

    private int getMonth(LocalDate date){
        String dates = String.valueOf(date);
        String[] datesToSplit = dates.split("-");
        String month = datesToSplit[2].charAt(0) == '0' ? String.valueOf(datesToSplit[2].charAt(1)) : datesToSplit[2];
        int monthNum = Integer.parseInt(month);
        return monthNum;
    }

    public String getStringFromDate(LocalDate date){
        String text = String.valueOf(date);
        String[] dates = text.split("-");
        String month = String.valueOf(date.getMonth());
        month = month.charAt(0) + month.substring(1, month.length()).toLowerCase();
        String day = String.valueOf(dates[2]);
        day = day.charAt(0) == '0' ? day.substring(1) : day;
        String answerToUser = date.getYear() + " year " + month + " " + day;
        return answerToUser;
    }
}
