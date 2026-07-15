package org.psyrioty.magicLeaders.Utils;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.psyrioty.magicLeaders.MagicLeaders;
import org.psyrioty.magicLeaders.Objects.Leader;
import org.psyrioty.magicLeaders.Objects.Leaderboard;

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
                for (Leader leader: leaderSet){
                    leaderboard.CheckValue(leader);
                }

                leaderboard.CheckPeriod();
            }
        }, 20L * 60L, 20L * 60L);
    }

    public static void Stop(){
        update.cancel();
        update = null;
    }
}
