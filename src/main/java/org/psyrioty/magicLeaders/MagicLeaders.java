package org.psyrioty.magicLeaders;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.psyrioty.magicLeaders.Commands.MainCommand;
import org.psyrioty.magicLeaders.Database.Requests;
import org.psyrioty.magicLeaders.GUI.LeaderboardMenu;
import org.psyrioty.magicLeaders.Listeners.GUIEvents;
import org.psyrioty.magicLeaders.Listeners.PlayerEvents;
import org.psyrioty.magicLeaders.Objects.Leader;
import org.psyrioty.magicLeaders.Objects.Leaderboard;
import org.psyrioty.magicLeaders.Utils.TaskLogic;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class MagicLeaders extends JavaPlugin implements Listener {
    static Set<LeaderboardMenu> leaderboardMenuSet = new HashSet<>();
    static MagicLeaders plugin;

    static Set<Leaderboard> leaderboards = new HashSet<>();
    static Set<Leader> leaders = new HashSet<>();

    PluginManager pm;

    @Override
    public void onEnable() {
        plugin = this;
        CheckPlaceholderAPI();

        registerEvents();

        //-----БД--------
        createDatabase(plugin);
        Requests.createTables();
        //================

        //-----КОМАНДЫ-----
        this.getCommand("leaders").setExecutor(new MagicLeaders());
        //=================

        getAllLeaderboards();

        TaskLogic.Update();
    }

    private void registerEvents(){
        pm = Bukkit.getPluginManager();
        pm.registerEvents(new GUIEvents(), plugin);
        pm.registerEvents(new PlayerEvents(), plugin);
    }

    @Override
    public void onDisable() {
        TaskLogic.Stop();
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

    public static Set<Leaderboard> getLeaderboards() {
        return leaderboards;
    }

    public static Set<LeaderboardMenu> getLeaderboardMenuSet() {
        return leaderboardMenuSet;
    }

    public static Set<Leader> getLeaders() {
        return leaders;
    }

    //---------------------------ПОЛУЧЕНИЕ ЛИДЕРБОРДОВ ИЗ ФАЙЛОВ YML------------------
    private void getAllLeaderboards(){
        List<File> leaderboardYmlFiles = getLeaderboardYmlFiles(plugin);

        for(File file: leaderboardYmlFiles){
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);

            String placeholder = config.getString("placeholder");
            int period = config.getInt("period");

            String stringDate = config.getString("startDate");
            String name = config.getString("name");

            String id = file.getName().toLowerCase().replace(".yml", "");

            if(
                    placeholder == null ||
                    period == 0 ||
                    stringDate == null ||
                    name == null
            ){
                continue;
            }

            LocalDate startDate = LocalDate.parse(stringDate);

            List<Leader> leadersTop = Requests.getTopLeaders(id);

            Leaderboard leaderboard = new Leaderboard(
                    placeholder,
                    period,
                    startDate,
                    name,
                    id,

                    checkLeader(leadersTop.get(0)),
                    checkLeader(leadersTop.get(1)),
                    checkLeader(leadersTop.get(2))
            );

            leaderboards.add(leaderboard);
        }
    }

    private Leader checkLeader(Leader leader){
        if(leader == null){
            return null;
        }

        for(Leader leaderOld: leaders){
            if(leaderOld.getUuid().equals(leader.getUuid())){
                return leaderOld;
            }
        }
        return leader;
    }

    private static List<File> getLeaderboardYmlFiles(JavaPlugin plugin) {
        File folder = new File(plugin.getDataFolder(), "Leaderboards");

        if (!folder.exists() && !folder.mkdirs()) {
            return new ArrayList<>();
        }

        File[] files = folder.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".yml");
            }
        });

        List<File> result = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    result.add(file);
                }
            }
        }

        return result;
    }
    //===================================================================================

    //---------------------------БАЗА ДАННЫХ----------------------------------
    private static void createDatabase(JavaPlugin plugin) {
        File databaseFolder = new File(plugin.getDataFolder(), "Database");

        if (!databaseFolder.exists()) {
            databaseFolder.mkdirs();
        }

        File databaseFile = new File(databaseFolder, "db.sqlite");

        if (!databaseFile.exists()) {
            try {
                databaseFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Не удалось создать db.sqlite");
                e.printStackTrace();
            }
        }
    }
    //==============================================================================
}
