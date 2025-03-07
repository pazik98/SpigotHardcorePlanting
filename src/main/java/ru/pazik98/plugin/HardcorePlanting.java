package ru.pazik98.plugin;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import ru.pazik98.command.CommandShp;
import ru.pazik98.entity.container.EntityStateContainer;
import ru.pazik98.entity.plant.PlantPreset;
import ru.pazik98.entity.plant.PlantPresetManager;
import ru.pazik98.listener.PlayerListener;
import ru.pazik98.listener.WorldListener;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

        this.getCommand("shp").setExecutor(new CommandShp());

        initConfigs();
        updateLoadedChunks();
        initEntityContainer();
    }

    @Override
    public void onDisable() {
        EntityStateContainer.getInstance().saveAll();
    }

    public void initConfigs() {
        this.saveDefaultConfig();

        String plantConfigName = "plant-config.yml";
        File plantConfigFile = new File(getDataFolder(), plantConfigName);
        YamlConfiguration plantConfig;

        // Try to read plant config
        if (!plantConfigFile.exists()) {
            getLogger().config(String.format("%s not found. Loading default config...", plantConfigName));
            saveResource(plantConfigName, false);
            plantConfig = YamlConfiguration.loadConfiguration(plantConfigFile);
        } else {
            try {
                plantConfig = YamlConfiguration.loadConfiguration(plantConfigFile);
            } catch (Exception e) {
                getLogger().severe(String.format("Can't read %s. Loading default config...", plantConfigName));
                try {
                    plantConfig = readConfigFromResource(plantConfigName);
                } catch (InvalidConfigurationException ie) {
                    e.printStackTrace();
                    this.onDisable();
                    return;
                }
            }
        }

        // Try to load plant config to PlantPresetManager
        try {
            PlantPresetManager.loadPresets(plantConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }

        getLogger().warning(PlantPresetManager.getPreset("Wheat").toString());
    }

    private YamlConfiguration readConfigFromResource(String resource) throws InvalidConfigurationException {
        InputStream inputStream = getResource(resource);
        String text = new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));
        var config = new YamlConfiguration();
        config.loadFromString(text);
        return config;
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
