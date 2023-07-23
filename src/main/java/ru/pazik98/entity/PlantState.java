package ru.pazik98.entity;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import ru.pazik98.util.Util;

import java.util.ArrayList;
import java.util.List;

public class PlantState {

    private PlantType plantType;
    private long plantingTick;
    private int growthPhase;
    private float maturity;
    private float productivity;
    private float decay;
    private Location location;

    private SoilState soil;
    private boolean isDead;

    public PlantState(PlantType plantType, SoilState soil, Location location, long plantingTick) {
        this.plantType = plantType;
        this.plantingTick = plantingTick;
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
