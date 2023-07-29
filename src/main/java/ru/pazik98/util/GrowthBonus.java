package ru.pazik98.util;

public enum GrowthBonus {
    GROWTH_SPEED(0, 0,0, 0,0.01f, -0.1f, 1.0f),
    MATURITY_SPEED(0, 0, 0, 0, 0.01f, -0.02f, 0.05f),
    DECAY_SPEED(0.015f, -0.01f, 0, 0, 0.01f, -0.05f, 0),
    DEATH_CHANCE(0.002f, 0.015f, 0,0, 0.005f, 0.01f, 0),
    CROP_AMOUNT(0.002f, -0.008f, 0, 0, 0, 0, 0.7f);

    private float humidityExcess;
    private float humidityDeficit;

    private float lightExcess;
    private float lightDeficit;

    private float temperatureExcess;
    private float temperatureDeficit;

    private float fertilizer;

    GrowthBonus(float humidityExcess, float humidityDeficit, float lightExcess, float lightDeficit,
                float temperatureExcess, float temperatureDeficit, float fertilizer) {
        this.humidityExcess = humidityExcess;
        this.humidityDeficit = humidityDeficit;
        this.lightExcess = lightExcess;
        this.lightDeficit = lightDeficit;
        this.temperatureExcess = temperatureExcess;
        this.temperatureDeficit = temperatureDeficit;
        this.fertilizer = fertilizer;
    }

    public float getHumidityExcess() {
        return humidityExcess;
    }

    public float getHumidityDeficit() {
        return humidityDeficit;
    }

    public float getLightExcess() {
        return lightExcess;
    }

    public float getLightDeficit() {
        return lightDeficit;
    }

    public float getTemperatureExcess() {
        return temperatureExcess;
    }

    public float getTemperatureDeficit() {
        return temperatureDeficit;
    }

    public float getFertilizer() {
        return fertilizer;
    }
}
