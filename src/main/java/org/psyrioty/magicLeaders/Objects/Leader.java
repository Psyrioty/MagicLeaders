package org.psyrioty.magicLeaders.Objects;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.psyrioty.magicLeaders.MagicLeaders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Leader {
    OfflinePlayer offlinePlayer;
    UUID uuid;
    HashMap<Leaderboard, Double> leaderboards = new HashMap<>(); //для наградок, потом буду смотреть есть ли что давать игроку при входе
    boolean rewardGave = false;

    public Leader(
            OfflinePlayer player
    ){
        this.offlinePlayer = player;
        this.uuid = player.getUniqueId();
    }

    public void giveReward(){
        Player player = offlinePlayer.getPlayer();

        if(player == null){
            return;
        }

        if(!player.isOnline() || player.isDead()){
            return;
        }


        for(Leaderboard leaderboard: leaderboards.keySet()){
            for(String command: leaderboard.getRewardCommands()){
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player_name%", player.getName()));
            }
        }
    }

    public UUID getUuid() {
        return uuid;
    }

    public OfflinePlayer getOfflinePlayer() {
        return offlinePlayer;
    }

    public void setPlayer(OfflinePlayer OfflinePlayer) {
        this.offlinePlayer = OfflinePlayer;
    }

    public boolean isRewardGave() {
        return rewardGave;
    }
}
