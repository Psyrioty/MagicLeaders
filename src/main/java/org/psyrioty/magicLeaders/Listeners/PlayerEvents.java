package org.psyrioty.magicLeaders.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.psyrioty.magicLeaders.Database.Requests;
import org.psyrioty.magicLeaders.MagicLeaders;
import org.psyrioty.magicLeaders.Objects.Leader;
import org.psyrioty.magicLeaders.Objects.Leaderboard;
import org.psyrioty.magicLeaders.Utils.APIHelper;
import org.psyrioty.magicLeaders.Utils.PlaceholderAPIPlugin;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PlayerEvents implements Listener {

    @EventHandler
    private void PlayerJoin(PlayerJoinEvent event){
        Set<Leader> leaders = MagicLeaders.getLeaders();

        Player player = event.getPlayer();

        if(player.hasPermission("magicleaders.admin")){
            return;
        }

        checkRewards(player);

        for(Leader leader: leaders){
            if(leader.getOfflinePlayer().getUniqueId().toString().equals(player.getUniqueId().toString())){
                //leader.setPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
                leader.setPlayer(Bukkit.getOfflinePlayer(player.getName()));
                return;
            }
        }

        Leader leader = new Leader(
            player
        );
        leaders.add(leader);

        Bukkit.getScheduler().runTaskAsynchronously(MagicLeaders.getPlugin(), () -> {
            Requests.addLeader(leader);



            for(Leaderboard leaderboard: leader.getLeaderboards().keySet()) {
                double value = PlaceholderAPIPlugin.getPlaceholderDouble(leaderboard.getPlaceholder(), player);

                Requests.addLeaderboard(
                        player.getUniqueId().toString(),
                        leaderboard.getName(),
                        value,
                        value
                );
            }
        });
    }

    private void checkRewards(Player player){
        Bukkit.getScheduler().runTaskAsynchronously(MagicLeaders.getPlugin(), () -> {
            List<String> rewards = Requests.getRewards(player.getUniqueId().toString());

            if(checkFreeItems(player) < rewards.size()){
                return;
            }

            for(String command: rewards){
                Bukkit.getScheduler().runTask(MagicLeaders.getPlugin(), () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("%player_name%", player.getName()));
                });
            }

            Requests.removeRewards(player.getUniqueId().toString());
        });
    }

    private int checkFreeItems(Player player){
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



    /*@EventHandler
    private void PlayerExit(PlayerQuitEvent event){
        Set<Leader> leaders = MagicLeaders.getLeaders();

        Player player = event.getPlayer();

        UUID uuid = player.getUniqueId();

        for(Leader leader: leaders){
            if(leader.getUuid().equals(player.getUniqueId())){
                leaders.remove(leader);
                return;
            }
        }
    }*/
}
