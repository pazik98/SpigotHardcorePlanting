package ru.pazik98.entity;

import org.bukkit.Location;
import org.bukkit.Material;

public class PlantState {

    private PlantType plantType;
    private int light;
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
        this.light = 0;
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

    @Override
    public String toString() {
        return "PlantState{" +
                "plantType=" + plantType +
                ", light=" + light +
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
