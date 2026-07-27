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
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;
import org.psyrioty.magicLeaders.MagicLeaders;
import org.psyrioty.magicLeaders.Objects.Leader;
import org.psyrioty.magicLeaders.Objects.LeaderValue;
import org.psyrioty.magicLeaders.Objects.Leaderboard;
import org.psyrioty.magicLeaders.Utils.APIHelper;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class LeaderboardMenu implements InventoryHolder {
    private Inventory inventory;

    public LeaderboardMenu(Player player){
        Leader leader = APIHelper.findLeaderForUUID(player.getUniqueId());

        createInventory(leader);
        player.openInventory(inventory);
        MagicLeaders.getLeaderboardMenuSet().add(this);
    }

    private void createInventory(Leader leader){
        this.inventory = Bukkit.createInventory(this, 54, "Топы");
        serializeInventory(leader);
    }

    private void serializeInventory(Leader leader){
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
            SkullMeta meta = (SkullMeta) itemStack.getItemMeta();

            meta.displayName(
                    mm.deserialize(
                            leaderboard.getName()
                    )
            );

            //текстура головы
            String hash = leaderboard.getTextureHash();

            if(!hash.isEmpty()) {
                PlayerProfile profile = Bukkit.createPlayerProfile(UUID.randomUUID());

                PlayerTextures textures = profile.getTextures();
                try {
                    textures.setSkin(new URL(
                            hash
                    ));
                } catch (MalformedURLException e) {
                    throw new RuntimeException(e);
                }

                profile.setTextures(textures);
                meta.setPlayerProfile((com.destroystokyo.paper.profile.PlayerProfile) profile);
            }
            //конец текстуры головы


            List<Component> lore = new ArrayList<>();

            HashMap<Leader, Double> tops = leaderboard.getTops();

            lore.add(mm.deserialize(
                    ""
            ));

            int k = 0;
            for(Leader leaderTop: tops.keySet()){
                lore.add(mm.deserialize(
                        "<#98FB98>"  + leaderTop.getOfflinePlayer().getName() + " <#3CB371>" + tops.get(leaderTop)
                ));

                k++;
                if(k > 2){
                    break;
                }
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

            if(leader != null) {
                int position = APIHelper.findTopPositionForLeader(leader, tops);

                lore.add(mm.deserialize(
                        "<#3CB371>" + (position + 1) + " <#98FB98>" + leader.getOfflinePlayer().getName() + " <#3CB371>" + tops.get(leader)
                ));
            }

            lore.add(mm.deserialize(
                    ""
            ));

            lore.add(mm.deserialize(
                    "<#98FB98>Осталось <#3CB371>" + leaderboard.getRemainingTime()
            ));

            meta.lore(lore);

            itemStack.setItemMeta(meta);
            inventory.setItem(i + 9, itemStack);
            i++;
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}
