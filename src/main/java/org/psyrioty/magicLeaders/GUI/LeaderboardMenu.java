package org.psyrioty.magicLeaders.GUI;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class LeaderboardMenu implements InventoryHolder {
    private Inventory inventory;

    LeaderboardMenu(Player player){
        createInventory();
        player.openInventory(inventory);
    }

    private void createInventory(){
        this.inventory = Bukkit.createInventory(this, 27);
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
