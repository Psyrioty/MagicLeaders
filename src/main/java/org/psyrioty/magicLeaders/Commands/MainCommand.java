package org.psyrioty.magicLeaders.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.psyrioty.magicLeaders.GUI.LeaderboardMenu;

public class MainCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if(!(sender instanceof Player)){
            sender.sendMessage("Эта команда только для игроков!");
            return true;
        }

        LeaderboardMenu leaderboardMenu = new LeaderboardMenu((Player) sender);

        return true;
    }
}
