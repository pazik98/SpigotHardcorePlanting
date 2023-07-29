package ru.pazik98.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import ru.pazik98.plugin.HardcorePlanting;
import ru.pazik98.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PlantManager {

    private static PlantManager instance;

    private final Logger logger = Bukkit.getLogger();
    private List<SoilState> soilList = new ArrayList();
    private List<PlantState> plantList = new ArrayList<>();
    private float tickFrequency = 0.1f;

    public PlantManager() {
        new BukkitRunnable() {
            @Override
            public void run() {
                tick();
            }
        }.runTaskTimer(HardcorePlanting.getInstance(), 1, 0);
    }

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

    public void removeSoil(Location location) {
        SoilState soil = getSoil(location);
        if (soil != null) {
            soilList.remove(soil);
            logger.log(Level.WARNING, "Soil removed " + soil.getLocation());
        }
    }

    public void createPlant(Material seedMaterial, Block soilBlock) {
        Location plantLocation = soilBlock.getLocation().clone().add(0, 1, 0);
        SoilState soil = getSoil(soilBlock.getLocation());
        if (soil == null) {
            logger.warning("Cannot create plant! Soil isn't exist.");
            return;
        }
        PlantState plant = new PlantState(PlantType.getPlantType(seedMaterial), soil,
                plantLocation, soilBlock.getWorld().getFullTime());
        plantList.add(plant);
        logger.log(Level.WARNING, "Created new plant! " + plant);
    }

    public PlantState getPlant(Location location) {
        for (PlantState plantState : plantList) {
            if (plantState.getLocation().equals(location)) return plantState;
        }
        return null;
    }

    public void removePlant(Location location) {
        PlantState plantState = getPlant(location);
        if (plantState != null) {
            plantList.remove(plantState);
        }
    }

    public void tick() {
        for (PlantState plant : plantList) {
            // Check for plant existing
            if (!plant.getPlantType().getPlantMaterial().equals(plant.getLocation().getBlock().getType())) {
                removePlant(plant.getLocation());
                return;
            }
            plant.incrementUpdatesTickNumber();
            if (Util.getRandom(tickFrequency)) plant.update();
        }
    }

    public static PlantManager getInstance() {
        if (instance == null) {
            instance = new PlantManager();
        }
        return instance;
    }
}
