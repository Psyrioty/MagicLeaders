package org.psyrioty.magicLeaders.Database;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.psyrioty.magicLeaders.MagicLeaders;
import org.psyrioty.magicLeaders.Objects.Leader;
import org.psyrioty.magicLeaders.Objects.LeaderValue;
import org.psyrioty.magicLeaders.Objects.Leaderboard;
import org.psyrioty.magicLeaders.Objects.Placeholder;
import org.psyrioty.magicLeaders.Utils.APIHelper;

import java.sql.*;
import java.util.*;

public class Requests {

    private static Connection connection;

    public static void connect(String databasePath) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        connection = DriverManager.getConnection(
                "jdbc:sqlite:" + databasePath
        );

        createTables();
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void disconnect() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void createTables() {
        try (Statement statement = connection.createStatement()) {

            statement.execute("""
                CREATE TABLE IF NOT EXISTS Leader (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    uuid TEXT NOT NULL UNIQUE,
                    name TEXT NOT NULL
                )
            """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS Leaderboard (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    leaderboardTag TEXT NOT NULL,
                    startValue REAL NOT NULL,
                    leaderId INTEGER NOT NULL,
                    FOREIGN KEY (leaderId) REFERENCES Leader(id)
                )
            """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS Reward (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    commands TEXT NOT NULL,
                    leaderId INTEGER NOT NULL,
                    FOREIGN KEY (leaderId) REFERENCES Leader(id)
                )
            """);

            statement.execute("""
                CREATE TABLE IF NOT EXISTS Placeholder (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    uuid TEXT NOT NULL,
                    placeholder TEXT NOT NULL,
                    value REAL NOT NULL
                )
            """);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addPlaceholder(String uuid, String placeholder, double value) {
        try (PreparedStatement statement = connection.prepareStatement("""
            INSERT INTO Placeholder(uuid, placeholder, value)
            VALUES(?, ?, ?)
            """)) {

            statement.setString(1, uuid);
            statement.setString(2, placeholder);
            statement.setDouble(3, value);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Placeholder> getPlaceholders() {
        List<Placeholder> placeholders = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement("""
            SELECT *
            FROM Placeholder
            """);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                placeholders.add(new Placeholder(
                        resultSet.getString("placeholder"),
                        Bukkit.getOfflinePlayer(UUID.fromString(resultSet.getString("uuid"))),
                        resultSet.getDouble("value")
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return placeholders;
    }

    public static void updatePlaceholderValue(String uuid, String placeholder, double value) {
        try (PreparedStatement statement = connection.prepareStatement("""
            UPDATE Placeholder
            SET value = ?
            WHERE uuid = ? AND placeholder = ?
            """)) {

            statement.setDouble(1, value);
            statement.setString(2, uuid);
            statement.setString(3, placeholder);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Set<Leader> getLeaders() {
        Set<Leader> leaders = new HashSet<>();

        try (PreparedStatement statement =
                     connection.prepareStatement("SELECT * FROM Leader");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String uuid = resultSet.getString("uuid");
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));

                leaders.add(new Leader(
                        offlinePlayer
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return leaders;
    }

    public static void addLeader(Leader leader) {
        OfflinePlayer offlinePlayer = leader.getOfflinePlayer();

        if(offlinePlayer == null){
            return;
        }

        try (PreparedStatement statement = connection.prepareStatement("""
            INSERT OR IGNORE INTO Leader(uuid, name)
            VALUES(?, ?)
            """)) {

            statement.setString(1, offlinePlayer.getUniqueId().toString());
            statement.setString(2, offlinePlayer.getName());

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<String> getRewards(String uuid) {
        List<String> commands = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement("""
            SELECT r.commands
            FROM Reward r
            JOIN Leader l ON r.leaderId = l.id
            WHERE l.uuid = ?
            """)) {

            statement.setString(1, uuid);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {

                    String rewardCommands = resultSet.getString("commands");

                    for (String command : rewardCommands.split("\\R")) {
                        command = command.trim();

                        if (!command.isEmpty()) {
                            commands.add(command);
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return commands;
    }

    public static void removeRewards(String uuid) {
        try (PreparedStatement statement = connection.prepareStatement("""
            DELETE FROM Reward
            WHERE leaderId = (
                SELECT id
                FROM Leader
                WHERE uuid = ?
            )
            """)) {

            statement.setString(1, uuid);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void addReward(String uuid, List<String> commands) {
        try (PreparedStatement leaderStatement = connection.prepareStatement(
                "SELECT id FROM Leader WHERE uuid = ?")) {

            leaderStatement.setString(1, uuid);

            try (ResultSet resultSet = leaderStatement.executeQuery()) {

                if (!resultSet.next()) {
                    return;
                }

                int leaderId = resultSet.getInt("id");

                try (PreparedStatement rewardStatement = connection.prepareStatement(
                        "INSERT INTO Reward(commands, leaderId) VALUES(?, ?)")) {
                    StringBuilder commandString = new StringBuilder();
                    for(String command: commands) {
                        commandString.append(command).append("\n");
                    }

                    rewardStatement.setString(1, commandString.toString());
                    rewardStatement.setInt(2, leaderId);

                    rewardStatement.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /*
    public static List<Leader> getTopLeaders(String leaderboardTag) {
        List<Leader> leaders = new ArrayList<>();

        try (PreparedStatement statement = connection.prepareStatement("""
            SELECT l.*
            FROM Leaderboard lb
            JOIN Leader l ON lb.leaderId = l.id
            WHERE lb.leaderboardTag = ?
            ORDER BY lb.value DESC
            LIMIT 3
            """)) {

            statement.setString(1, leaderboardTag);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String uuid = resultSet.getString("uuid");
                    Leader leader = APIHelper.findLeaderForUUID(uuid);
                    if(leader == null) {
                        leaders.add(new Leader(
                                Bukkit.getOfflinePlayer(UUID.fromString(uuid))
                        ));
                    }else{
                        leaders.add(leader);
                    }
                }
            }

            while (leaders.size() < 3){
                leaders.add(null);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return leaders;
    }
    */

    public static void addLeaderboard(String uuid, String leaderboardTag, double startValue) {
        try (PreparedStatement leaderStatement = connection.prepareStatement(
                "SELECT id FROM Leader WHERE uuid = ?")) {

            leaderStatement.setString(1, uuid);

            try (ResultSet resultSet = leaderStatement.executeQuery()) {

                if (!resultSet.next()) {
                    return;
                }

                int leaderId = resultSet.getInt("id");

                try (PreparedStatement leaderboardStatement = connection.prepareStatement("""
                    INSERT INTO Leaderboard(leaderboardTag, startValue, leaderId)
                    VALUES(?, ?, ?)
                    """)) {

                    leaderboardStatement.setString(1, leaderboardTag);
                    leaderboardStatement.setDouble(2, startValue);
                    leaderboardStatement.setInt(3, leaderId);

                    leaderboardStatement.executeUpdate();
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateStartValue(String uuid, String leaderboardTag, double startValue) {
        try (PreparedStatement statement = connection.prepareStatement("""
            UPDATE Leaderboard
            SET startValue = ?
            WHERE leaderboardTag = ?
              AND leaderId = (
                  SELECT id
                  FROM Leader
                  WHERE uuid = ?
              )
            """)) {

            statement.setDouble(1, startValue);
            statement.setString(2, leaderboardTag);
            statement.setString(3, uuid);

            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static LeaderValue getLeaderboard(Leader leader, String leaderboardTag) {
        try (PreparedStatement statement = connection.prepareStatement("""
            SELECT lb.*
            FROM Leaderboard lb
            JOIN Leader l ON lb.leaderId = l.id
            WHERE l.uuid = ? AND lb.leaderboardTag = ?
            """)) {

            String uuid = leader.getOfflinePlayer().getUniqueId().toString();

            statement.setString(1, uuid);
            statement.setString(2, leaderboardTag);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    LeaderValue leaderValue = new LeaderValue(
                            resultSet.getDouble("startValue")
                    );
                    return leaderValue;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}