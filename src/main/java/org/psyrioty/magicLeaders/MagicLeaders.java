package org.psyrioty.magicLeaders;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class MagicLeaders extends JavaPlugin {

    MagicLeaders plugin;

    @Override
    public void onEnable() {
        plugin = this;
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Plugin getPlugin(){
        return plugin;
    }
}
