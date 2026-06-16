package org.psyrioty.magicLeaders.Objects;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Leaderboard {
    String placeholder;
    int period; //количество дней для сброса
    LocalDate startDate; //когда начался отсчет
    List<String> rewardCommands = new ArrayList<>(); //скорей всего так и сделаю, или хз..

    Leaderboard(
            String placeholder,
            int period,
            LocalDate startDate
    ){
        this.placeholder = placeholder;
        this.period = period;
        this.startDate = startDate;
    }

    public void CheckPeriod(){
        if (!LocalDate.now().isBefore(startDate.plusDays(period))) {

        }
    }

    public List<String> getRewardCommands() {
        return rewardCommands;
    }
}
