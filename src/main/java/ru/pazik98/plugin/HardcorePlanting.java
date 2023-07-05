package ru.pazik98.plugin;

import org.bukkit.plugin.java.JavaPlugin;
import ru.pazik98.listener.PlayerListener;
import ru.pazik98.listener.WorldListener;

public class HardcorePlanting extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new WorldListener(), this);
    }

    @Override
    public void onDisable() {

    }
}
