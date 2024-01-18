package ru.pazik98.plugin;

import org.bukkit.plugin.java.JavaPlugin;
import ru.pazik98.db.SQLiteConnectionManager;
import ru.pazik98.listener.PlayerListener;
import ru.pazik98.listener.WorldListener;

import java.sql.*;
import java.util.logging.Level;

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
        initDatabse();
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

    private void initDatabse() {
        SQLiteConnectionManager connectionManager = SQLiteConnectionManager.getInstance();
        try {
            connectionManager.createTables();
        } catch (SQLException e) {
            getLogger().warning("Cannot create database!");
            throw new RuntimeException(e);
        }
    }
}
