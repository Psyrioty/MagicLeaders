package org.psyrioty.magicLeaders.GUI;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class LeaderboardMenu implements InventoryHolder {
    private Inventory inventory;

    LeaderboardMenu(Player player){

    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
