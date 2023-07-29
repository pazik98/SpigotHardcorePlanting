package ru.pazik98.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import ru.pazik98.util.Convert;
import ru.pazik98.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PlantState {

    private PlantType plantType;
    private long plantingTick;
    private long updatesTickNumber;
    private int growthPhase;
    private float maturity;
    private float productivity;
    private float decay;
    private Location location;

    private SoilState soil;
    private boolean isDead;

    private final Logger logger = Bukkit.getLogger();

    public PlantState(PlantType plantType, SoilState soil, Location location, long plantingTick) {
        this.plantType = plantType;
        this.plantingTick = plantingTick;
        this.updatesTickNumber = 0;
        this.growthPhase = 0;
        this.maturity = 0f;
        this.productivity = 0f;
        this.decay = 0f;
        this.soil = soil;
        this.location = location;
        this.isDead = false;
    }

    public void update() {
        grow();
    }

    private void grow() {
        Ageable ageable = (Ageable) this.getLocation().getBlock().getBlockData();
        if (ageable.getAge() != ageable.getMaximumAge()) {
            ageable.setAge(ageable.getAge() + 1);
            this.getLocation().getBlock().setBlockData(ageable);
            growthPhase++;
        }
    }

    public void incrementUpdatesTickNumber() {
        updatesTickNumber++;
    }

    public void incrementUpdatesTickNumber(int ticks) {
        updatesTickNumber += ticks;
    }

    public long getUpdatesTickNumber() {
        return updatesTickNumber;
    }

    public float getHappiness() {
        float happiness = 100.0f;
        // light reason
        float light = 1f;
        if (getLocation().getBlock().getLightFromSky() < getPlantType().getExpectedLight()) light = 0.0f;
        // water reason
        float water = 1.0f - Math.abs(getPlantType().getExpectedHumidity() / 100 - getSoil().getHumidity());
        // temperature reason
        float temperature = 1.0f - Math.abs(Convert.degreesToTemperature(getPlantType().getExpectedTemperature()) - getSoil().getTemperature()) / 4;
        System.out.println("--happiness: " + light + " " + water + " " + temperature);
        return (happiness + water + temperature) / 3;
    }

    public Location getLocation() {
        return location;
    }

    public SoilState getSoil() {
        return soil;
    }

    public List<ItemStack> getCrops() {
        List<ItemStack> harvest = new ArrayList<>();
        ItemStack seeds = new ItemStack(plantType.getSeedMaterial(), 2);
        ItemStack crop = new ItemStack(plantType.getPlantMaterial(), 1);
        harvest.add(seeds);
        harvest.add(crop);
        return harvest;
    }

    public PlantType getPlantType() {
        return plantType;
    }

    public long getPlantingTick() {
        return plantingTick;
    }

    public int getGrowthPhase() {
        return growthPhase;
    }

    public float getMaturity() {
        return maturity;
    }

    public float getProductivity() {
        return productivity;
    }

    public float getDecay() {
        return decay;
    }

    public boolean isDead() {
        return isDead;
    }

    @Override
    public String toString() {
        return "PlantState{" +
                "plantType=" + plantType +
                ", plantingTick=" + plantingTick +
                ", growthPhase=" + growthPhase +
                ", maturity=" + maturity +
                ", productivity=" + productivity +
                ", decay=" + decay +
                ", location=" + location +
                ", soil=" + soil +
                ", isDead=" + isDead +
                '}';
    }
}
