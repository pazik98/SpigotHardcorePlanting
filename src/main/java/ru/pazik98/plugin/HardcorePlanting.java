package ru.pazik98.plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.pazik98.db.SQLiteConnectionManager;
import ru.pazik98.entity.PlantState;
import ru.pazik98.entity.PlantType;
import ru.pazik98.listener.PlayerListener;
import ru.pazik98.listener.WorldListener;

import java.sql.*;

public class HardcorePlanting extends JavaPlugin {

    private static HardcorePlanting instance;

    public HardcorePlanting() {
        instance = this;
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new WorldListener(), this);
        initConfig();
        initDatabase();

        System.out.println(SQLiteConnectionManager.getInstance().getSoils(Bukkit.getWorlds().get(0).getChunkAt(0,0)));
        System.out.println(SQLiteConnectionManager.getInstance().getPlants(Bukkit.getWorlds().get(0).getChunkAt(0,0)));
    }

    @Override
    public void onDisable() {

    }

    public static HardcorePlanting getInstance() {
        return instance;
    }

    public void initConfig() {
        this.saveConfig();
    }

    private void initDatabase() {
        SQLiteConnectionManager connectionManager = SQLiteConnectionManager.getInstance();
        try {
            connectionManager.createTables();
        } catch (SQLException e) {
            getLogger().warning("Cannot create database!");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
