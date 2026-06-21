package org.psyrioty.magicLeaders.Objects;

import org.bukkit.Bukkit;
import org.psyrioty.magicLeaders.MagicLeaders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Leaderboard {
    String placeholder;
    int period; //количество дней для сброса
    LocalDate startDate; //когда начался отсчет
    List<String> rewardCommands = new ArrayList<>(); //скорей всего так и сделаю, или хз..
    String name;
    String id;

    public Leaderboard(
            String placeholder,
            int period,
            LocalDate startDate,
            String name,
            String id
    ){
        this.placeholder = placeholder;
        this.period = period;
        this.startDate = startDate;
        this.name = name;
        this.id = id;
    }

    public void CheckPeriod(){
        if (!LocalDate.now().isBefore(startDate.plusDays(period))) {
            try{

            }catch (Exception exception){
                Bukkit.getLogger().severe("MagicLeaders error Leaderboard.java CheckPeriod() " + exception.getMessage());
            }
        }
    }

    public List<String> getRewardCommands() {
        return rewardCommands;
    }
}
