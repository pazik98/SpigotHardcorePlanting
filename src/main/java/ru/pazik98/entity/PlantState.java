package ru.pazik98.entity;

import org.bukkit.Location;
import org.bukkit.Material;

public class PlantState {

    private Material type;
    private int light;
    private int age;
    private int growthPhase;
    private float maturity;
    private float productivity;
    private float decay;

    private Location location;

    private SoilState soil;

    public PlantState(Material type, SoilState soil, Location location) {
        this.type = type;
        this.light = 0;
        this.age = 0;
        this.growthPhase = 0;
        this.maturity = 0f;
        this.productivity = 0f;
        this.decay = 0f;
        this.soil = soil;
        this.location = location;
    }

    public void update() {
        age += 1;
        grow();
    }

    private void grow() {

    }

    @Override
    public String toString() {
        return "PlantState{" +
                "type=" + type +
                ", light=" + light +
                ", age=" + age +
                ", growthPhase=" + growthPhase +
                ", maturity=" + maturity +
                ", productivity=" + productivity +
                ", decay=" + decay +
                ", location=" + location +
                ", soil=" + soil +
                '}';
    }
}
