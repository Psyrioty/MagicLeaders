package org.psyrioty.magicLeaders.Objects;

import org.bukkit.Bukkit;
import org.psyrioty.magicLeaders.MagicLeaders;
import org.psyrioty.magicLeaders.Utils.PlaceholderAPIPlugin;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Leaderboard {
    String placeholder;
    int period; //количество дней для сброса
    LocalDate startDate; //когда начался отсчет
    List<String> rewardCommands = new ArrayList<>(); //скорей всего так и сделаю, или хз..
    String name;
    String id;


    //-----ТОПЫ----
    HashMap<Leader, Double> tops = new HashMap<>();
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

        if(topOne != null) {
            List<Double> values = topOne.getLeaderboards().get(this);

            double value = 0;

            if(values != null){
                if(values.size() == 2){
                    value = values.getLast() - values.getFirst();
                }
            }

            tops.put(topOne, value);
        }
        if(topTwo != null) {
            List<Double> values = topTwo.getLeaderboards().get(this);

            double value = 0;

            if(values != null){
                if(values.size() == 2){
                    value = values.getLast() - values.getFirst();
                }
            }

            tops.put(topTwo, value);
        }
        if(topThree != null) {
            List<Double> values = topThree.getLeaderboards().get(this);

            double value = 0;

            if(values != null){
                if(values.size() == 2){
                    value = values.getLast() - values.getFirst();
                }
            }

            tops.put(topThree, value);
        }
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
                for(Leader leader: tops.keySet()){
                    leader.giveReward();
                }
            }catch (Exception exception){
                Bukkit.getLogger().severe("MagicLeaders error Leaderboard.java CheckPeriod() " + exception.getMessage());
            }
        }
    }

    public void CheckValue(Leader leader){
        Bukkit.getLogger().info(leader.getOfflinePlayer() + "");

        double value = PlaceholderAPIPlugin.getPlaceholderDouble(placeholder, leader.getOfflinePlayer());

        HashMap<Leaderboard, List<Double>> leaderboards = leader.getLeaderboards();

        List<Double> values = leaderboards.get(this);

        if(values == null){
            values = new ArrayList<>();
            values.add(value);
            values.add(value);
        }else{
            values.remove(1);
            values.add(value);
        }

        leaderboards.put(this, values);

        checkLeads(leader, values);
    }

    private void checkLeads(Leader leader, List<Double> values){

        HashMap<Leader, Double> newTops = new HashMap<>();

        boolean replace = false;

        double value = values.getLast() - values.getFirst();

        Bukkit.getLogger().info(values.getLast() + " " + values.getFirst());

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

    public List<String> getRewardCommands() {
        return rewardCommands;
    }
}
