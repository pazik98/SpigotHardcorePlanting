package ru.pazik98.entity.plant;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.HashMap;

public class PlantPresetManager {

    private static final HashMap<String, PlantPreset> presetsMap = new HashMap<>();

    public static PlantPreset getPreset(String name) {
        return presetsMap.get(name);
    }

    public static void loadPresets(YamlConfiguration config) {
        for (String key : config.getKeys(false)) {
            presetsMap.put(
                    config.getString(key + ".name"),
                    PlantPreset.builder()
                            .name(config.getString(key + ".name"))
                            .blockMaterial(Material.valueOf(config.getString(key + ".block-material")))
                            .seedMaterial(Material.valueOf(config.getString(key + ".seed-material")))
                            .harvestMaterial(Material.valueOf(config.getString(key + ".harvest-material")))
                            .growthStageCount(config.getInt(key + ".growth-stages"))
                            .maturationStageCount(config.getInt(key + ".maturation-stages"))
                            .decayStageCount(config.getInt(key + ".decay-stages"))
                            .expectedLight(config.getInt(key + ".expected-light"))
                            .expectedHumidity((float) config.getDouble(key + ".expected-humidity"))
                            .expectedTemperature((float) config.getDouble(key + ".expected-temperature"))
                            .expectedHarvestCount((float) config.getDouble(key + ".expected-harvest-count"))
                            .expectedSeedCount((float) config.getDouble(key + ".expected-seed-count"))
                            .growthWaterCost((float) config.getDouble(key + ".growth-water-cost"))
                            .growthFertilizerCost((float) config.getDouble(key + ".growth-fertilizer-cost"))
                            .growthTicksCost((float) config.getDouble(key + ".growth-ticks-cost"))
                            .maturationWaterCost((float) config.getDouble(key + ".maturation-water-cost"))
                            .maturationFertilizerCost((float) config.getDouble(key + ".maturation-fertilizer-cost"))
                            .maturationTicksCost((float) config.getDouble(key + ".maturation-ticks-cost"))
                            .decayFertilizerCost((float) config.getDouble(key + ".decay-fertilizer-cost"))
                            .decayTicksCost((float) config.getDouble(key + ".decay-ticks-cost"))
                            .build()
            );
        }
    }
}
