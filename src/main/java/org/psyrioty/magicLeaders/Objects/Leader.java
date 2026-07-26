package org.psyrioty.magicLeaders.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.psyrioty.magicLeaders.Database.Requests;
import org.psyrioty.magicLeaders.MagicLeaders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Leader {
    private OfflinePlayer offlinePlayer;
    private HashMap<Leaderboard, LeaderValue> leaderboards = new HashMap<>(); //очки у определенного лидерборда
    private boolean rewardGave = false;
    private String name;

    public Leader(
            OfflinePlayer player
    ){
        this.offlinePlayer = player;
    }

    public void giveReward(List<String> commands){
        Player player = offlinePlayer.getPlayer();

        if(player == null){
            addReward(commands);
            return;
        }

        if(!player.isOnline() || player.isDead() || checkFreeItems() < commands.size()){
            addReward(commands);
            return;
        }


        for(String command: commands){
            Bukkit.getScheduler().runTask(MagicLeaders.getPlugin(), () -> {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("%player_name%", player.getName()));
            });
        }
    }

    private int checkFreeItems(){
        if(!offlinePlayer.isOnline()){
            return -1;
        }

        Player player = offlinePlayer.getPlayer();

        Inventory inventory = player.getInventory();


        int freeItems = 0;

        for(int i = 0; i < 36; i++){
            ItemStack itemStack = inventory.getItem(i);

            if(itemStack == null){
                freeItems++;
                continue;
            }

            if(itemStack.getType() == Material.AIR){
                freeItems++;
            }
        }

        return freeItems;
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
