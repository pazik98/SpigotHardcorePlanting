package ru.pazik98.db;

import ru.pazik98.plugin.HardcorePlanting;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
}
