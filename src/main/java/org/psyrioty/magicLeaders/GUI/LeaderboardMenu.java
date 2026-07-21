package org.psyrioty.magicLeaders.GUI;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
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
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class LeaderboardMenu implements InventoryHolder {
    private Inventory inventory;

    public LeaderboardMenu(Player player){
        createInventory();
        player.openInventory(inventory);
        MagicLeaders.getLeaderboardMenuSet().add(this);
    }

    private void createInventory(){
        this.inventory = Bukkit.createInventory(this, 54, "Топы");
        serializeInventory();
    }

    private void serializeInventory(){
        for(int i = 0; i < 9; i++){
            ItemStack itemStack = new ItemStack(Material.RED_STAINED_GLASS_PANE);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(" ");

            /*
            MiniMessage mm = MiniMessage.miniMessage();
            Component component = mm.deserialize(
                    "<#DDA0DD>Неизвестно<#DDA0DD>"
            );
            itemMeta.displayName(component);
            */

            itemStack.setItemMeta(itemMeta);

            inventory.setItem(i, itemStack);
            inventory.setItem(i + 45, itemStack);
        }

        int i = 0;
        Set<Leaderboard> leaderboardSet = MagicLeaders.getLeaderboards();

        for (Leaderboard leaderboard: leaderboardSet){
            MiniMessage mm = MiniMessage.miniMessage();

            ItemStack itemStack = new ItemStack(Material.PLAYER_HEAD);
            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.displayName(
                    mm.deserialize(
                            leaderboard.getName()
                    )
            );


            List<Component> lore = new ArrayList<>();

            HashMap<Leader, Double> tops = leaderboard.getTops();

            lore.add(mm.deserialize(
                    ""
            ));

            for(Leader leader: tops.keySet()){
                lore.add(mm.deserialize(
                        "<#98FB98>"  + leader.getOfflinePlayer().getName() + " <#3CB371>" + tops.get(leader)
                ));
            }

            if(tops.size() < 3){
                for (int j = tops.size(); j < 3; j++){
                    lore.add(mm.deserialize(
                            "<#98FB98>Неизвестно"
                    ));
                }
            }

            lore.add(mm.deserialize(
                    ""
            ));

            itemMeta.lore(lore);

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
