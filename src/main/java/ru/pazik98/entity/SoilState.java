package ru.pazik98.entity;

import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import ru.pazik98.db.repository.data.SoilStateData;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class SoilState implements Soil {

    private float humidity;
    private float fertilizer;
    private float temperature;
    private float water;
    private float waterCapacity = 1000f;
    private Location location;

    @Setter
    private Plant plant;

    public SoilState(SoilStateData soilStateData) {
        this.humidity = soilStateData.getHumidity();
        this.fertilizer = soilStateData.getFertilizer();
        this.temperature = soilStateData.getTemperature();
        this.water = soilStateData.getWater();
        this.location = new Location(
                Bukkit.getWorld(soilStateData.getWorldUID()),
                soilStateData.getX(),
                soilStateData.getY(),
                soilStateData.getZ()
        );
        if (soilStateData.getPlant() != null) this.plant = new PlantState(soilStateData.getPlant());
    }

    @Override
    public void increaseFertilizer(float count) {
        fertilizer += count;
    }

    @Override
    public void decreaseFertilizer(float count) {
        fertilizer -= count;
    }

    @Override
    public void increaseWater(float count) {
        water += count;
    }

    @Override
    public void decreaseWater(float count) {
        water -= count;
    }

    public static SoilState from(SoilStateData soilStateData) {
        return new SoilState(soilStateData);
    }
}
