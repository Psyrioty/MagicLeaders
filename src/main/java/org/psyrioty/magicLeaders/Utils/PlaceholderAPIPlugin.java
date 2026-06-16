package org.psyrioty.magicLeaders.Utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

public class PlaceholderAPIPlugin {
    public static String getPlaceholderString(String placeholder, OfflinePlayer player) {
        if(placeholder == null){
            return null;
        }

        if(player == null){
            return null;
        }

        String text = PlaceholderAPI.setPlaceholders(player, placeholder);

        return text;
    }

    public static int getPlaceholderInteger(String placeholder, OfflinePlayer player) {
        if(placeholder == null){
            return 0;
        }

        if(player == null){
            return 0;
        }

        int value = 0;
        try {
            value = Integer.parseInt(PlaceholderAPI.setPlaceholders(player, placeholder));
        }catch (Exception exception){
            return 0;
        }

        return value;
    }

    public static double getPlaceholderDouble(String placeholder, OfflinePlayer player) {
        if(placeholder == null){
            return 0;
        }

        if(player == null){
            return 0;
        }

        double value = 0;
        try {
            value = Double.parseDouble(PlaceholderAPI.setPlaceholders(player, placeholder));
        }catch (Exception exception){
            return 0;
        }

        return value;
    }
}
