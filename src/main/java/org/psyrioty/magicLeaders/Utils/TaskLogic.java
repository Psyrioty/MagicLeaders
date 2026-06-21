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

    public static void Update(){
        update = Bukkit.getScheduler().runTaskTimerAsynchronously(MagicLeaders.getPlugin(), () -> {
            for(Leaderboard leaderboard: MagicLeaders.getLeaderboards()){
                leaderboard.CheckPeriod();
            }

            for(Leader leader: leaderSet){
                if(leader.isRewardGave()){
                    leader.giveReward();
                }
            }
        }, 20L * 60L, 20L * 60L);
    }

    public static void Stop(){
        update.cancel();
        update = null;
    }
}
