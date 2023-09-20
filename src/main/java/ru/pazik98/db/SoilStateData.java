package ru.pazik98.db;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import ru.pazik98.entity.SoilState;

import java.util.UUID;

public class SoilStateData {

    private int x;
    private int y;
    private int z;
    private UUID worldId;
    private float humidity;
    private float temperature;
    private float fertilizer;
    private float water;

    SoilStateData(SoilState soilState) {
        this.x = soilState.getLocation().getBlockX();
        this.y = soilState.getLocation().getBlockY();
        this.z = soilState.getLocation().getBlockZ();
        this.worldId = soilState.getLocation().getWorld().getUID();
        this.humidity = soilState.getHumidity();
        this.temperature = soilState.getTemperature();
        this.fertilizer = soilState.getFertilizer();
        this.water = soilState.getWater();
    }

    public SoilState toEntity() {
        return new SoilState(
                humidity,
                fertilizer,
                temperature,
                water,
                1000f,
                new Location(Bukkit.getWorld(worldId), x, y, z)
        );
    }
}
