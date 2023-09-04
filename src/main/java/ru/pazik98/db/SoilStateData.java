package ru.pazik98.db;

import jakarta.persistence.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import ru.pazik98.entity.SoilState;

import java.util.UUID;

@Entity
@Table(name="farmlands")
public class SoilStateData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name="humidity")
    private float humidity;

    @Column(name="fertilizer")
    private float fertilizer;

    @Column(name="temperature")
    private float temperature;

    @Column(name="water")
    private float water;

    @Column(name="water_capacity")
    private float waterCapacity;

    @Column(name="x")
    private int x;

    @Column(name="y")
    private int y;

    @Column(name="z")
    private int z;

    @Column(name="world_id")
    private UUID worldId;

    public SoilStateData(SoilState soilState) {
        this.humidity = soilState.getHumidity();
        this.fertilizer = soilState.getFertilizer();
        this.temperature = soilState.getTemperature();
        this.water = soilState.getWater();
        this.waterCapacity = soilState.getWaterCapacity();
        this.x = soilState.getLocation().getBlockX();
        this.y = soilState.getLocation().getBlockY();
        this.z = soilState.getLocation().getBlockZ();
        this.worldId = soilState.getLocation().getWorld().getUID();
    }

    public SoilState toEntity() {
        return new SoilState(humidity, fertilizer, temperature, water, waterCapacity,
                new Location(Bukkit.getWorld(worldId), x, y, z));
    }
}
