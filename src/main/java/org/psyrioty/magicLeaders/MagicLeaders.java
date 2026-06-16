package org.psyrioty.magicLeaders;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public final class MagicLeaders extends JavaPlugin implements Listener {

    static MagicLeaders plugin;

    @Override
    public void onEnable() {
        plugin = this;
        CheckPlaceholderAPI();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Plugin getPlugin(){
        return plugin;
    }

    private void CheckPlaceholderAPI(){
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            Bukkit.getPluginManager().registerEvents(plugin, this); //
        } else {
            getLogger().severe("Could not find PlaceholderAPI! This plugin is required."); //
            Bukkit.getPluginManager().disablePlugin(this);
        }
    }
}
