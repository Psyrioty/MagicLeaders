package org.psyrioty.magicLeaders.Database;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.psyrioty.magicLeaders.Objects.Leader;
import org.psyrioty.magicLeaders.Objects.Leaderboard;

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
                    value REAL NOT NULL,
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
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

                leaders.add(new Leader(
                        offlinePlayer
                ));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return leaders;
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
                    leaders.add(new Leader(
                            Bukkit.getOfflinePlayer(resultSet.getString("uuid"))
                    ));
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
}