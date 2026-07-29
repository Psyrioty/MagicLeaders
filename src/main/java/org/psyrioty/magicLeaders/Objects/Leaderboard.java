package org.psyrioty.magicLeaders.Objects;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.psyrioty.magicLeaders.Database.Requests;
import org.psyrioty.magicLeaders.MagicLeaders;
import org.psyrioty.magicLeaders.Utils.PlaceholderAPIPlugin;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Leaderboard {
    String placeholder;
    int period; //количество дней для сброса
    LocalDate startDate; //когда начался отсчет


    List<String> rewardCommandsTopOne = new ArrayList<>(); //скорей всего так и сделаю, или хз..
    List<String> rewardCommandsTopTwo = new ArrayList<>(); //скорей всего так и сделаю, или хз..
    List<String> rewardCommandsTopThree = new ArrayList<>(); //скорей всего так и сделаю, или хз..


    String name;
    String id;
    FileConfiguration config;
    File file;

    String textureHash = "";


    //-----ТОПЫ----
    HashMap<Leader, Double> tops = new HashMap<>();
    //=============

    public Leaderboard(
            String placeholder,
            int period,
            LocalDate startDate,
            String name,
            String id,
            List<String> rewardCommandsTopOne,
            List<String> rewardCommandsTopTwo,
            List<String> rewardCommandsTopThree,
            FileConfiguration config,
            File file,

            Leader topOne,
            Leader topTwo,
            Leader topThree,

            String textureHash
    ){
        this.placeholder = placeholder;
        this.period = period;
        this.startDate = startDate;
        this.name = name;
        this.id = id;
        this.rewardCommandsTopOne = rewardCommandsTopOne;
        this.rewardCommandsTopTwo = rewardCommandsTopTwo;
        this.rewardCommandsTopThree = rewardCommandsTopThree;
        this.config = config;
        this.file = file;

        if(textureHash != null) {
            this.textureHash = textureHash;
        }

        if(topOne != null) {
            LeaderValue values = topOne.getLeaderboards().get(this);

            double value = 0;

            if(values != null){
                value = values.getResult();
            }

            tops.put(topOne, value);
        }
        if(topTwo != null) {
            LeaderValue values = topTwo.getLeaderboards().get(this);

            double value = 0;

            if(values != null){
                value = values.getResult();
            }

            tops.put(topTwo, value);
        }
        if(topThree != null) {
            LeaderValue values = topThree.getLeaderboards().get(this);

            double value = 0;

            if(values != null){
                value = values.getResult();
            }

            tops.put(topThree, value);
        }
    }

    public String getTextureHash() {
        return textureHash;
    }

    public String getName() {
        return name;
    }

    public HashMap<Leader, Double> getTops() {
        return tops;
    }

    public void CheckPeriod(){
        if (!LocalDate.now().isBefore(startDate.plusDays(period))) {
            try{

                resetAll();

                int i = 0;
                for(Leader leader: tops.keySet()){
                    switch (i){
                        case 0:
                            leader.giveReward(rewardCommandsTopOne);
                            break;
                        case 1:
                            leader.giveReward(rewardCommandsTopTwo);
                            break;
                        case 2:
                            leader.giveReward(rewardCommandsTopThree);
                            break;
                    }

                    if(i > 2){
                        break;
                    }
                    i++;
                }
            }catch (Exception exception){
                Bukkit.getLogger().severe("MagicLeaders error Leaderboard.java CheckPeriod() " + exception.getMessage());
            }
        }
    }

    private void resetAll() throws IOException {
        startDate = LocalDate.now();

        for(Leader leader: MagicLeaders.getLeaders()){
            if(!leader.getOfflinePlayer().isOnline()){
                Bukkit.getScheduler().runTaskAsynchronously(MagicLeaders.getPlugin(), () -> {
                    Requests.removeLeaderboard(
                            leader.getOfflinePlayer().getUniqueId().toString(),
                            name
                    );
                });

                //leader.getLeaderboards().remove(this);

                HashMap<Leaderboard, LeaderValue> leaderValueHashMap = leader.getLeaderboards();

                LeaderValue leaderValue = leaderValueHashMap.get(this);

                LeaderValue newLeaderValue = new LeaderValue(
                        leaderValue.getResult(),
                        leaderValue.getResult()
                );

                leaderValueHashMap.put(this, newLeaderValue);

                continue;
            }

            double startValue = PlaceholderAPIPlugin.getPlaceholderDouble(placeholder, leader.getOfflinePlayer());

            config.set("startDate", startDate.toString());
            config.save(file);

            LeaderValue leaderValueNew = new LeaderValue(
                    startValue,
                    startValue
            );

            leader.getLeaderboards().put(this, leaderValueNew);
            Bukkit.getScheduler().runTaskAsynchronously(MagicLeaders.getPlugin(), () -> {
                Requests.updateStartValue(
                        leader.getOfflinePlayer().getUniqueId().toString(),
                        name,
                        startValue,
                        startValue
                );
            });
        }
    }

    public void CheckValue(Leader leader){
        if(!leader.getOfflinePlayer().isOnline()){
            LeaderValue values = leader.getLeaderboards().get(this);

            if(values == null){
                return;
            }

            //checkLeads(leader, values);

            tops.put(leader, values.getResult());

            return;
        }

        double value = PlaceholderAPIPlugin.getPlaceholderDouble(placeholder, leader.getOfflinePlayer());

        HashMap<Leaderboard, LeaderValue> leaderboards = leader.getLeaderboards();

        LeaderValue values = leaderboards.get(this);

        if(values == null){
            values = new LeaderValue(value, value);
            values.setValue(value);

            OfflinePlayer player = leader.getOfflinePlayer();

            Requests.addLeaderboard(
                    player.getUniqueId().toString(),
                    name,
                    value,
                    value
            );
        }else{
            values.setValue(value);

            Requests.updateValue(
                    leader.getOfflinePlayer().getUniqueId().toString(),
                    name,
                    value
            );
        }

        leaderboards.put(this, values);

        tops.put(leader, values.getResult());

        //checkLeads(leader, values);
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void topsSort(){
        tops = tops.entrySet().stream()
                .sorted(Map.Entry.<Leader, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    /*
    private void checkLeads(Leader leader, LeaderValue values){

        HashMap<Leader, Double> newTops = new HashMap<>();

        boolean replace = false;

        double value = values.getResult();

        if(tops.size() < 3){
            tops.put(leader, value);
            
            tops = tops.entrySet().stream()
                    .sorted(Map.Entry.<Leader, Double>comparingByValue().reversed())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));

            return;
        }

        for(Leader leaderTop: tops.keySet()){
            double valueTop = tops.get(leaderTop);


            if(value > valueTop && !replace){
                replace = true;

                //tops.remove(leaderTop);
                newTops.put(leader, value);
            }else{
                newTops.put(leaderTop, valueTop);
            }
        }

        if(!replace){
            tops = tops.entrySet().stream()
                    .sorted(Map.Entry.<Leader, Double>comparingByValue().reversed())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new
                    ));

            return;
        }

        tops = newTops.entrySet().stream()
                .sorted(Map.Entry.<Leader, Double>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
        //tops = newTops;
    }
    */

    public String getRemainingTime() {
        LocalDate today = LocalDate.now();

        LocalDate nextReset = startDate;

        while (!nextReset.isAfter(today)) {
            nextReset = nextReset.plusDays(period);
        }

        LocalDateTime resetTime = nextReset.atStartOfDay();

        Duration duration = Duration.between(LocalDateTime.now(), resetTime);

        long days = duration.toDays();
        long hours = duration.toHoursPart();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        return String.format("%d д. %d ч. %d м. %d с.",
                days, hours, minutes, seconds);
    }
}
