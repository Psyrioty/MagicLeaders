package org.psyrioty.magicLeaders.Database;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.psyrioty.magicLeaders.Objects.Leader;
import org.psyrioty.magicLeaders.Objects.Leaderboard;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
}