package ru.pazik98.db;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import ru.pazik98.entity.PlantState;
import ru.pazik98.entity.PlantType;
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
            "`plant_id` INTEGER NOT NULL," +
            "PRIMARY KEY(id)" +
            ");";

    private final String PLANT_TABLE_SQL = "CREATE TABLE IF NOT EXISTS `plant` (" +
            "`id`INTEGER NOT NULL UNIQUE," +
            "`x`INTEGER NOT NULL," +
            "`y`INTEGER NOT NULL," +
            "`z`INTEGER NOT NULL," +
            "`world_id`TEXT NOT NULL," +
            "`seed_material`TEXT NOT NULL," +
            "`planting_tick`INTEGER NOT NULL," +
            "`update_ticks`INTEGER NOT NULL," +
            "`growth_phase`INTEGER NOT NULL," +
            "`maturity`REAL NOT NULL," +
            "`productivity`REAL NOT NULL," +
            "`decay`REAL NOT NULL," +
            "`soil_id`INTEGER NOT NULL," +
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

    public List<SoilState> getSoils(Chunk chunk) {
        List<SoilState> soils = new ArrayList<>();

        String query = String.format(
                "SELECT * FROM soil WHERE x >= '%d' AND x < '%d' AND z >= '%d' AND z < '%d' AND world_id = '%s'",
                chunk.getX() * 16,
                chunk.getX() * 16 + 15,
                chunk.getZ() * 16,
                chunk.getZ() * 16 + 15,
                chunk.getWorld().getUID()
        );
        System.out.println(query);

        try {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (rs == null) {
                return soils;
            }
            while (rs.next()) {
                SoilState soil = new SoilState(
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
                        )
                );
                soils.add(soil);
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

    public List<PlantState> getPlants(Chunk chunk) {
        List<PlantState> plants = new ArrayList<>();

        String query = String.format(
                "SELECT * FROM plant WHERE x >= '%d' AND x < '%d' AND z >= '%d' AND z < '%d' AND world_id = '%s'",
                chunk.getX() * 16,
                chunk.getX() * 16 + 15,
                chunk.getZ() * 16,
                chunk.getZ() * 16 + 15,
                chunk.getWorld().getUID()
        );
        System.out.println(query);

        try {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (rs == null) {
                return plants;
            }
            while (rs.next()) {
                PlantState plant = new PlantState(
                        PlantType.getPlantType(Material.matchMaterial(rs.getString("seed_material"))),
                        new Location(
                                Bukkit.getWorld(UUID.fromString(rs.getString("world_id"))),
                                rs.getInt("x"),
                                rs.getInt("y"),
                                rs.getInt("z")
                        ),
                        rs.getLong("planting_tick"),
                        rs.getLong("update_ticks"),
                        rs.getInt("growth_phase"),
                        rs.getFloat("maturity"),
                        rs.getFloat("productivity"),
                        rs.getFloat("decay")
                );
                plants.add(plant);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return plants;
    }

    public void addPlant(PlantState plant) {
        String query = String.format(
                "INSERT INTO plant (x, y, z, world_id, seed_material, planting_tick, update_ticks, growth_phase, maturity, productivity, decay) " +
                        "VALUES ('%d', '%d', '%d', '%s', '%s', '%d', '%d', '%d', '%f', '%f', '%f')",
                plant.getLocation().getBlockX(),
                plant.getLocation().getBlockY(),
                plant.getLocation().getBlockZ(),
                plant.getLocation().getWorld().getUID().toString(),
                plant.getPlantType().getSeedMaterial(),
                plant.getPlantingTick(),
                plant.getUpdatesTickNumber(),
                plant.getGrowthPhase(),
                plant.getMaturity(),
                plant.getProductivity(),
                plant.getDecay()
        );

        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void updatePlant(PlantState plant) {
        String query = String.format(
                "UPDATE plant SET update_ticks = '%d', growth_phase = '%d', maturity = '%f', productivity = '%f', decay = '%f' " +
                        "WHERE x = '%d' AND y = '%d' AND z = '%d' AND world_id = '%s'",
                plant.getUpdatesTickNumber(),
                plant.getGrowthPhase(),
                plant.getMaturity(),
                plant.getProductivity(),
                plant.getDecay(),
                plant.getLocation().getBlockX(),
                plant.getLocation().getBlockY(),
                plant.getLocation().getBlockZ(),
                plant.getLocation().getWorld().getUID().toString()
        );

        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    public void removePlant(PlantState plant) {
        String query = String.format(
                "DELETE FROM plant WHERE x = '%d' AND y = '%d' AND z = '%d' AND world_id = '%s'",
                plant.getLocation().getBlockX(),
                plant.getLocation().getBlockY(),
                plant.getLocation().getBlockZ(),
                plant.getLocation().getWorld().getUID().toString()
        );

        try {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }
}
