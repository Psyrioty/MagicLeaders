package org.psyrioty.magicLeaders.GUI;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.psyrioty.magicLeaders.MagicLeaders;
import org.psyrioty.magicLeaders.Objects.Leader;
import org.psyrioty.magicLeaders.Objects.Leaderboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class LeaderboardMenu implements InventoryHolder {
    private Inventory inventory;

    public LeaderboardMenu(Player player){
        createInventory();
        player.openInventory(inventory);
    }

    private void createInventory(){
        this.inventory = Bukkit.createInventory(this, 54);
        serializeInventory();
    }

    private void serializeInventory(){
        for(int i = 0; i < 9; i++){
            ItemStack itemStack = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName("");
            itemStack.setItemMeta(itemMeta);

            inventory.setItem(i, itemStack);
            inventory.setItem(i + 45, itemStack);
        }

        int i = 0;
        Set<Leaderboard> leaderboardSet = MagicLeaders.getLeaderboards();

        for (Leaderboard leaderboard: leaderboardSet){
            ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(leaderboard.getName());

            List<String> lore = new ArrayList<>();
            lore.add("");
            Leader one = leaderboard.getTopOne();

            if(one != null) {
                lore.add(one.getOfflinePlayer().getName());
            }else{
                lore.add("Неизвестно");
            }

            Leader two = leaderboard.getTopTwo();

            if(two != null) {
                lore.add(two.getOfflinePlayer().getName());
            }else{
                lore.add("Неизвестно");
            }

            Leader three = leaderboard.getTopThree();

            if(three != null) {
                lore.add(three.getOfflinePlayer().getName());
            }else{
                lore.add("Неизвестно");
            }

            itemMeta.setLore(lore);

            itemStack.setItemMeta(itemMeta);
            inventory.setItem(i + 9, itemStack);
            i++;
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
