package org.psyrioty.magicLeaders.Objects;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.psyrioty.magicLeaders.Database.Requests;
import org.psyrioty.magicLeaders.MagicLeaders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Leader {
    private OfflinePlayer offlinePlayer;
    private UUID uuid;
    private HashMap<Leaderboard, LeaderValue> leaderboards = new HashMap<>(); //очки у определенного лидерборда
    private boolean rewardGave = false;
    private String name;

    public Leader(
            OfflinePlayer player
    ){
        this.offlinePlayer = player;
        this.uuid = player.getUniqueId();
    }

    public void giveReward(List<String> commands){
        Player player = offlinePlayer.getPlayer();

        if(player == null){
            addReward(commands);
            return;
        }

        if(!player.isOnline() || player.isDead()){
            addReward(commands);
            return;
        }


        for(String command: commands){
            Bukkit.getScheduler().runTask(MagicLeaders.getPlugin(), () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player_name%", player.getName()));
            });
        }
    }

    private void addReward(List<String> commands){
        Bukkit.getScheduler().runTaskAsynchronously(MagicLeaders.getPlugin(), () -> {
            Requests.addReward(
                    offlinePlayer.getUniqueId().toString(),
                    commands
            );
        });
    }

    public HashMap<Leaderboard, LeaderValue> getLeaderboards() {
        return leaderboards;
    }

    public UUID getUuid() {
        return uuid;
    }

    public OfflinePlayer getOfflinePlayer() {
        return offlinePlayer;
    }

    public void setPlayer(OfflinePlayer OfflinePlayer) {
        this.offlinePlayer = OfflinePlayer;
        this.uuid = offlinePlayer.getUniqueId();
    }

    public boolean isRewardGave() {
        return rewardGave;
    }
}
