package org.psyrioty.magicLeaders.Utils;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.psyrioty.magicLeaders.MagicLeaders;

public class PlaceholderAPIExpansion extends PlaceholderExpansion {
    private final MagicLeaders plugin; //

    public PlaceholderAPIExpansion(MagicLeaders plugin) {
        this.plugin = plugin;
    }

    @Override
    @NotNull
    public String getAuthor() {
        return String.join(", ", plugin.getDescription().getAuthors()); //
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "magicleaders";
    }

    @Override
    @NotNull
    public String getVersion() {
        return plugin.getDescription().getVersion(); //
    }

    @Override
    public boolean persist() {
        return true; //
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) {
            return null;
        }



       /* if (params.equalsIgnoreCase("friends")) { //
            if (plugin.areFriends(one, two)) {
                return ChatColor.GREEN + one.getName() + " and " + two.getName() + " are friends!";
            } else {
                return ChatColor.RED + one.getName() + " and " + two.getName() + " are not friends!";
            }
        }

        */
        return null;
    }
}
