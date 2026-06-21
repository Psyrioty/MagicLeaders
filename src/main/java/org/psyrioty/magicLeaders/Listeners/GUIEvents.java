package org.psyrioty.magicLeaders.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.psyrioty.magicLeaders.GUI.LeaderboardMenu;
import org.psyrioty.magicLeaders.MagicLeaders;

import java.util.Set;

public class GUIEvents implements Listener {
    @EventHandler
    private void onClick(InventoryClickEvent event){
        Inventory inventory = event.getInventory();
        if(!(inventory.getHolder() instanceof LeaderboardMenu)){
            return;
        }

        event.setCancelled(true);
    }

    /*
    @EventHandler
    private void onClose(InventoryCloseEvent event){
        Inventory inventory = event.getInventory();
        if(!(inventory.getHolder() instanceof LeaderboardMenu)){
            return;
        }

        Set<LeaderboardMenu> leaderboardMenuSet = MagicLeaders.getLeaderboardMenuSet();

        for(LeaderboardMenu leaderboardMenu: leaderboardMenuSet){
            if(leaderboardMenu.getInventory() == inventory){
                leaderboardMenuSet.remove(leaderboardMenu);
                return;
            }
        }
    }
    */
}
