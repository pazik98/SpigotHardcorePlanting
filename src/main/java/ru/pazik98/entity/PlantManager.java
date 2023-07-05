package ru.pazik98.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.plugin.PluginLogger;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlantManager {
    private static PlantManager instance;

    private final Logger logger = Bukkit.getLogger();
    private List<SoilState> soilList = new ArrayList();
    private List<PlantState> plantList = new ArrayList<>();

    public void createSoil(Block block) {
        SoilState soil = new SoilState((float) block.getHumidity(), (float) block.getTemperature(), block.getLocation());
        soilList.add(soil);
        logger.log(Level.WARNING, "Created new soil! " + soil);
    }

    public SoilState getSoil(Location location) {
        for (SoilState soil : soilList) {
            if (location.equals(soil.getLocation())) return soil;
        }
        return null;
    }

    public void createPlant(Material seedMaterial, Block soilBlock) {
        Location plantLocation = soilBlock.getLocation().clone().add(0, 1, 0);
        PlantState plant = new PlantState(PlantType.getPlantType(seedMaterial), getSoil(soilBlock.getLocation()),
                plantLocation, soilBlock.getWorld().getFullTime());
        plantList.add(plant);
        logger.log(Level.WARNING, "Created new plant! " + plant);
    }

    public static PlantManager getInstance() {
        if (instance == null) {
            instance = new PlantManager();
        }
        return instance;
    }
}
