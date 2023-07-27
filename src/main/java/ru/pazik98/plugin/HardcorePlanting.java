package ru.pazik98.plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.pazik98.listener.PlayerListener;
import ru.pazik98.listener.WorldListener;

public class HardcorePlanting extends JavaPlugin {

    private static HardcorePlanting instance;

    public HardcorePlanting() {
        instance = this;
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new WorldListener(), this);
    }

    @Override
    public void onDisable() {

    }

    public static HardcorePlanting getInstance() {
        return instance;
    }
}
