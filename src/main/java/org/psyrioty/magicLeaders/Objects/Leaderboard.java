package org.psyrioty.magicLeaders.Objects;

import org.bukkit.Bukkit;
import org.psyrioty.magicLeaders.MagicLeaders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Leaderboard {
    String placeholder;
    int period; //количество дней для сброса
    LocalDate startDate; //когда начался отсчет
    List<String> rewardCommands = new ArrayList<>(); //скорей всего так и сделаю, или хз..
    String name;
    String id;


    //-----ТОПЫ----
    Leader topOne;
    Leader topTwo;
    Leader topThree;
    //=============

    public Leaderboard(
            String placeholder,
            int period,
            LocalDate startDate,
            String name,
            String id,

            Leader topOne,
            Leader topTwo,
            Leader topThree
    ){
        this.placeholder = placeholder;
        this.period = period;
        this.startDate = startDate;
        this.name = name;
        this.id = id;

        this.topOne = topOne;
        this.topTwo = topTwo;
        this.topThree = topThree;
    }

    public String getName() {
        return name;
    }

    public Leader getTopOne() {
        return topOne;
    }

    public Leader getTopThree() {
        return topThree;
    }

    public Leader getTopTwo() {
        return topTwo;
    }

    public void CheckPeriod(){
        if (!LocalDate.now().isBefore(startDate.plusDays(period))) {
            try{
                topOne.giveReward();
                topTwo.giveReward();
                topThree.giveReward();
            }catch (Exception exception){
                Bukkit.getLogger().severe("MagicLeaders error Leaderboard.java CheckPeriod() " + exception.getMessage());
            }
        }
    }

    public List<String> getRewardCommands() {
        return rewardCommands;
    }
}
