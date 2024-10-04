package ru.pazik98.plugin;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.pazik98.entity.container.EntityStateContainer;
import ru.pazik98.listener.PlayerListener;
import ru.pazik98.listener.WorldListener;

public class HardcorePlanting extends JavaPlugin {

    @Getter
    private static HardcorePlanting instance;

    public HardcorePlanting() {
        instance = this;
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new WorldListener(), this);

        initConfig();
        initEntityContainer();
    }

    @Override
    public void onDisable() {

    }

    public void initConfig() {
        this.saveConfig();
    }

    public void initEntityContainer() {
        new BukkitRunnable() {
            @Override
            public void run() {
                EntityStateContainer.getInstance().update();
            }
        }.runTaskTimer(HardcorePlanting.getInstance(), 1, 0);
    }
}
