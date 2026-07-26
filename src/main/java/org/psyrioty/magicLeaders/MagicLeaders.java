package org.psyrioty.magicLeaders;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
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
import org.psyrioty.magicLeaders.Objects.LeaderValue;
import org.psyrioty.magicLeaders.Objects.Leaderboard;
import org.psyrioty.magicLeaders.Objects.Placeholder;
import org.psyrioty.magicLeaders.Utils.APIHelper;
import org.psyrioty.magicLeaders.Utils.PlaceholderAPIPlugin;
import org.psyrioty.magicLeaders.Utils.TaskLogic;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    static Set<Placeholder> placeholders = new HashSet<>();

    @Override
    public void onEnable() {
        plugin = this;
        CheckPlaceholderAPI();

        registerEvents();

        //-----БД--------
        createDatabase(plugin);
        try {
            Requests.connect(plugin.getDataPath() + "/Database/db.sqlite");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Requests.createTables();

        leaders = Requests.getLeaders();
        getAllLeaderboards();
        createAllLeaderValue();
        getAllPlaceholders();
        //================

        //-----КОМАНДЫ-----
        this.getCommand("leaders").setExecutor(new MainCommand());
        //=================


        TaskLogic.Update();
    }

    private void createAllLeaderValue(){
        for(Leader leader: leaders){
            for(Leaderboard leaderboard: leaderboards){
                LeaderValue leaderValue = Requests.getLeaderboard(leader, leaderboard.getName());

                if(leaderValue == null){
                    Bukkit.getLogger().severe("MagicLeaders пустое значение");
                    continue;
                }

                if(leader.getOfflinePlayer() == null) {
                    continue;
                }

                if(leader.getOfflinePlayer().getName() == null) {
                    continue;
                }

                leader.getLeaderboards().put(leaderboard, leaderValue);
            }
        }
    }

    private void registerEvents(){
        pm = Bukkit.getPluginManager();
        pm.registerEvents(new GUIEvents(), plugin);
        pm.registerEvents(new PlayerEvents(), plugin);
    }

    @Override
    public void onDisable() {
        TaskLogic.Stop();
        for(LeaderboardMenu leaderboardMenu: leaderboardMenuSet){
            if(leaderboardMenu == null){
                continue;
            }

            leaderboardMenu.getInventory().close();
        }
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

            String textureHash = config.getString("textureHash");

            List<String> commandsTopOne = config.getStringList("commands.one");
            List<String> commandsTopTwo = config.getStringList("commands.two");
            List<String> commandsTopThree = config.getStringList("commands.three");

            String id = file.getName().toLowerCase().replace(".yml", "");

            Bukkit.getLogger().info(
                    placeholder + "\n" +
                            period + "\n" +
                            stringDate + "\n" +
                            name + "\n"
            );

            if(
                    placeholder == null ||
                    period == 0 ||
                    stringDate == null ||
                    name == null
            ){
                continue;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate startDate = LocalDate.parse(stringDate, formatter);

            //List<Leader> leadersTop = Requests.getTopLeaders(id);

            Leaderboard leaderboard = new Leaderboard(
                    placeholder,
                    period,
                    startDate,
                    name,
                    id,
                    commandsTopOne,
                    commandsTopTwo,
                    commandsTopThree,
                    config,
                    file,

                    null,
                    null,
                    null,

                    textureHash
            );

            leaderboards.add(leaderboard);
        }
    }

    public static Set<Placeholder> getPlaceholders() {
        return placeholders;
    }

    private Leader checkLeader(Leader leader){
        if(leader == null){
            return null;
        }

        for(Leader leaderOld: leaders){
            if(leaderOld.getOfflinePlayer().getUniqueId().toString().equals(leader.getOfflinePlayer().getUniqueId().toString())){
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

    //---------------------------ПЛЭЙСХОЛЕДРЫ---------------------------------
    private static void getAllPlaceholders(){
        List<File> placeholderFiles = getPlaceholdersYmlFiles();

        for (File file: placeholderFiles){
            try{
                YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);

                String name = file.getName().replace(".yml", "");
                String placeholder = yamlConfiguration.getString("placeholder");
            }catch (Exception exception){}
        }
    }

    private static List<File> getPlaceholdersYmlFiles() {
        File folder = new File(plugin.getDataFolder(), "Placeholders");

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
    //========================================================================

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
