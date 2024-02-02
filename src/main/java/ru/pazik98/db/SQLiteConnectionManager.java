package ru.pazik98.db;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import ru.pazik98.entity.SoilState;
import ru.pazik98.plugin.HardcorePlanting;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SQLiteConnectionManager {

    private final String URL = "jdbc:sqlite:" + HardcorePlanting.getInstance().getDataFolder().getAbsolutePath() + "\\HardcorePlanting.db";

    private final String SOIL_TABLE_SQL = "CREATE TABLE IF NOT EXISTS`soil` (" +
            "`id`INTEGER NOT NULL UNIQUE," +
            "`x`INTEGER NOT NULL," +
            "`y`INTEGER NOT NULL," +
            "`z`INTEGER NOT NULL," +
            "`world_id`TEXT NOT NULL," +
            "`humidity`REAL NOT NULL," +
            "`temperature`REAL NOT NULL," +
            "`fertilizer`REAL NOT NULL," +
            "`water`REAL NOT NULL," +
            "PRIMARY KEY(id)" +
            ");";

    private final String PLANT_TABLE_SQL = "CREATE TABLE IF NOT EXISTS `plant` (" +
            "`id`INTEGER NOT NULL UNIQUE," +
            "`x`INTEGER NOT NULL," +
            "`y`INTEGER NOT NULL," +
            "`z`INTEGER NOT NULL," +
            "`seed_material`TEXT NOT NULL," +
            "`planting_tick`REAL NOT NULL," +
            "`temperature`REAL NOT NULL," +
            "`fertilizer`REAL NOT NULL," +
            "`water`REAL NOT NULL," +
            "PRIMARY KEY(id)" +
            ");";

    private static SQLiteConnectionManager instance;
    private Connection conn;

    private SQLiteConnectionManager() {
        try {
            connect();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static SQLiteConnectionManager getInstance() {
        if (instance == null) {
            instance = new SQLiteConnectionManager();
        }
        return instance;
    }

    private void connect() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        conn = DriverManager.getConnection(URL);
    }

    public void disconnect() throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    public void createTables() throws SQLException {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(SOIL_TABLE_SQL);
        stmt.executeUpdate(PLANT_TABLE_SQL);
    }

    public List<SoilState> getSoils(int chunkX, int chunkY) {
        List<SoilState> soils = new ArrayList<>();

        String query = String.format(
                "SELECT * FROM soil WHERE x >= '%d' AND x < '%d' AND z >= '%d' AND z < '%d'",
                chunkX * 16,
                chunkX * 16 + 15,
                chunkY * 16,
                chunkY * 16 + 15
        );

        try {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (rs == null) {

            }
            while (rs.next()) {
                soils.add(new SoilState(
                        rs.getFloat("humidity"),
                        rs.getFloat("fertilizer"),
                        rs.getFloat("temperature"),
                        rs.getFloat("water"),
                        1000f,
                        new Location(
                                Bukkit.getWorld(UUID.fromString(rs.getString("world_id"))),
                                rs.getInt("x"),
                                rs.getInt("y"),
                                rs.getInt("z")
                        ))
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return soils;
    }

    private void addSoil(SoilState soil) {
        String query = String.format(
                "INSERT INTO soil (x, y, z, world_id, humidity, temperature, fertilizer, water) " +
                        "VALUES ('%d', '%d', '%d', '%s', '%f', '%f', '%f', '%f')",
                soil.getLocation().getBlockX(),
                soil.getLocation().getBlockY(),
                soil.getLocation().getBlockZ(),
                soil.getLocation().getWorld().getUID().toString(),
                soil.getHumidity(),
                soil.getTemperature(),
                soil.getFertilizer(),
                soil.getWater()
        );

        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void addSoils(List<SoilState> soils) {
        for (SoilState soil : soils) {
            addSoil(soil);
        }
    }

    public void updateSoil(SoilState soil) {
        String query = String.format(
                "UPDATE soil SET humidity = '%f', temperature = '%f', fertilizer = '%f', water = '%f' " +
                        "WHERE x = '%d' AND y = '%d' AND z = '%d' AND world_id = '%s'",
                soil.getHumidity(),
                soil.getTemperature(),
                soil.getFertilizer(),
                soil.getWater(),
                soil.getLocation().getBlockX(),
                soil.getLocation().getBlockY(),
                soil.getLocation().getBlockZ(),
                soil.getLocation().getWorld().getUID().toString()
        );

        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void removeSoil(SoilState soil) {
        String query = String.format(
                "DELETE FROM soil WHERE x = '%d' AND y = '%d' AND z = '%d' AND world_id = '%s'",
                soil.getLocation().getBlockX(),
                soil.getLocation().getBlockY(),
                soil.getLocation().getBlockZ(),
                soil.getLocation().getWorld().getUID().toString()
        );

        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}
