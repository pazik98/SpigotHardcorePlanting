package ru.pazik98.plugin;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.pazik98.entity.container.EntityStateContainer;
import ru.pazik98.listener.PlayerListener;
import ru.pazik98.listener.WorldListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

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
        updateLoadedChunks();
        initEntityContainer();
    }

    @Override
    public void onDisable() {
        EntityStateContainer.getInstance().saveAll();
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

    public void updateLoadedChunks() {
        Set<Chunk> chunks = new HashSet<>();
        Bukkit.getWorlds().forEach(x -> chunks.addAll(Arrays.stream(x.getLoadedChunks()).toList()));
        EntityStateContainer.getInstance().load(chunks);
    }
}
