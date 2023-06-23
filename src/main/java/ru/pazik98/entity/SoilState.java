package ru.pazik98.entity;

import org.bukkit.Location;

public class SoilState {

    private float humidity;
    private float fertilizer;
    private float temperature;
    private Location location;

    private PlantState plant;

    public SoilState(float humidity, float temperature, Location location) {
        this.humidity = humidity;
        this.temperature = temperature;
        this.fertilizer = 0f;
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

    public void increaseHumidity(float count) {
        humidity += count;
    }

    public void descreaseHumidity(float count) {
        humidity -= count;
    }

    public void increaseFertilizer(float count) {
        fertilizer += count;
    }

    public void descreaseFertilizer(float count) {
        fertilizer -= count;
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
                ", location=" + location +
                '}';
    }
}
