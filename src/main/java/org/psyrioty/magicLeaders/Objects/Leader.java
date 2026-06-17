package org.psyrioty.magicLeaders.Objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.psyrioty.magicLeaders.MagicLeaders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Leader {
    Player player;
    UUID uuid;
    List<Leaderboard> leaderboards = new ArrayList<>(); //для наградок, потом буду смотреть есть ли что давать игроку при входе

    public Leader(
            Player player
    ){
        this.player = player;
        this.uuid = player.getUniqueId();
    }

    public void giveReward(){
        if(player == null){
            return;
        }

        if(!player.isOnline() || player.isDead()){
            return;
        }


        for(Leaderboard leaderboard: leaderboards){
            for(String command: leaderboard.getRewardCommands()){
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player_name%", player.getName()));
            }
        }
    }
}
