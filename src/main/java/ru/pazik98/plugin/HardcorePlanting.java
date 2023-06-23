package ru.pazik98.plugin;

import org.bukkit.plugin.java.JavaPlugin;
import ru.pazik98.PlayerListener;

public class HardcorePlanting extends JavaPlugin {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {

    }
}
