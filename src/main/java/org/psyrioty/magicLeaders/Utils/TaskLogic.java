package org.psyrioty.magicLeaders.Utils;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.psyrioty.magicLeaders.MagicLeaders;
import org.psyrioty.magicLeaders.Objects.Leader;
import org.psyrioty.magicLeaders.Objects.Leaderboard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class TaskLogic {
    static BukkitTask update;
    static Set<Leader> leaderSet;
    static Set<Leaderboard> leaderboardSet;

    public TaskLogic(){
        leaderSet = MagicLeaders.getLeaders();
        leaderboardSet = MagicLeaders.getLeaderboards();
    }

    public static void Update(){
        update = Bukkit.getScheduler().runTaskTimerAsynchronously(MagicLeaders.getPlugin(), () -> {
            if (leaderboardSet == null){
                leaderboardSet = MagicLeaders.getLeaderboards();
            }

            if (leaderSet == null){
                leaderSet = MagicLeaders.getLeaders();
            }

            for(Leaderboard leaderboard: leaderboardSet){
                HashMap<Leader, Double> oldTops = leaderboard.getTops(); //старые топы

                for (Leader leader: leaderSet){
                    leaderboard.CheckValue(leader);
                }

                leaderboard.CheckPeriod();

                HashMap<Leader, Double> newTops = leaderboard.getTops(); //новые топы, для уведов

                checkUpdateTops(
                        oldTops,
                        newTops,
                        leaderboard
                );
            }
        }, 20L * 60L, 20L * 60L);
    }

    private static void checkUpdateTops(
            HashMap<Leader, Double> oldTops,
            HashMap<Leader, Double> newTops,
            Leaderboard leaderboard
    ){
        List<Leader> oldPositions = new ArrayList<>();
        List<Leader> newPositions = new ArrayList<>();

        int i = 0;
        for(Leader leader: oldTops.keySet()){
            oldPositions.add(leader);

            i++;
            if(i >= 3){
                break;
            }
        }

        i = 0;
        for(Leader leader: newTops.keySet()){
            newPositions.add(leader);

            i++;
            if(i >= 3){
                break;
            }
        }

        HashMap<Leader, Integer> messageLeaders = new HashMap<>();
        i = 0;
        for(Leader leader: newPositions){
            if(oldPositions.size() <= i){
                break;
            }

            if(leader != oldPositions.get(i)){
                messageLeaders.put(leader, i);
            }

            i++;
        }

        if(messageLeaders.isEmpty()){
            return;
        }

        Messages.newTopMessage(messageLeaders, leaderboard);
    }

    public static void Stop(){
        update.cancel();
        update = null;
    }
}
