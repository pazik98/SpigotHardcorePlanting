package ru.pazik98.entity;

import org.bukkit.Location;

public class SoilState {

    private float humidity;
    private float fertilizer;
    private float temperature;
    private Location location;

    public SoilState(float humidity, float temperature, Location location) {
        this.humidity = humidity;
        this.temperature = temperature;
        this.fertilizer = 0f;
        this.location = location;
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
