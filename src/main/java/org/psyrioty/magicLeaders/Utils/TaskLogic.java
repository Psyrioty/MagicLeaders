package org.psyrioty.magicLeaders.Utils;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.psyrioty.magicLeaders.MagicLeaders;

public class TaskLogic {
    static BukkitTask update;

    public static void Update(){
        update = Bukkit.getScheduler().runTaskTimerAsynchronously(MagicLeaders.getPlugin(), () -> {

        }, 20L * 60L, 20L * 60L);
    }

    public static void Stop(){
        update.cancel();
        update = null;
    }
}
