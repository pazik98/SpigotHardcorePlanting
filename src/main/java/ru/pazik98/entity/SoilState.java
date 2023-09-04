package ru.pazik98.entity;

import org.bukkit.Location;

public class SoilState {

    private float humidity;
    private float fertilizer;
    private float temperature;
    private float water;
    private float waterCapacity;
    private Location location;

    private PlantState plant;

    public SoilState(float humidity, float temperature, Location location) {
        this.humidity = humidity;
        this.temperature = temperature;
        this.fertilizer = 0f;;
        this.waterCapacity = 1000f;
        this.water = waterCapacity * humidity;
        this.location = location;
        this.plant = null;
    }

    public SoilState(float humidity, float fertilizer, float temperature, float water, float waterCapacity, Location location) {
        this.humidity = humidity;
        this.fertilizer = fertilizer;
        this.temperature = temperature;
        this.water = water;
        this.waterCapacity = waterCapacity;
        this.location = location;
        this.plant = null;
    }

    public float getHumidity() {
        return humidity;
    }

    public float getFertilizer() {
        return fertilizer;
    }

    public float getTemperature() {
        return temperature;
    }

    public void increaseFertilizer(float count) {
        fertilizer += count;
    }

    public void decreaseFertilizer(float count) {
        fertilizer -= count;
    }

    public void increaseWater(float count) {
        water += count;
    }

    public void decreaseWater(float count) {
        water -= count;
    }

    public float getWater() {
        return water;
    }

    public float getWaterCapacity() {
        return waterCapacity;
    }

    public Location getLocation() {
        return location;
    }

    public PlantState getPlant() {
        return plant;
    }

    public void setPlant(PlantState plant) {
        this.plant = plant;
    }

    @Override
    public String toString() {
        return "SoilState{" +
                "humidity=" + humidity +
                ", fertilizer=" + fertilizer +
                ", temperature=" + temperature +
                ", water=" + water + "/" + waterCapacity +
                ", location=" + location +
                ", plant=" + plant +
                '}';
    }
}
