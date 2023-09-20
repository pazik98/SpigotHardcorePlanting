package ru.pazik98.db;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import ru.pazik98.entity.PlantState;
import ru.pazik98.entity.PlantType;

import java.util.UUID;

public class PlantStateData {
    private int x;
    private int y;
    private int z;
    private UUID worldId;
    private String seedMaterial;
    private long plantingTick;
    private long updateTicks;
    private int growthPhase;
    private float maturity;
    private float productivity;
    private float decay;

    PlantStateData(PlantState plantState) {
        this.x = plantState.getLocation().getBlockX();
        this.y = plantState.getLocation().getBlockY();
        this.z = plantState.getLocation().getBlockZ();
        this.worldId = plantState.getLocation().getWorld().getUID();
        this.seedMaterial = plantState.getPlantType().getSeedMaterial().toString();
        this.plantingTick = plantState.getPlantingTick();
        this.updateTicks = plantState.getUpdatesTickNumber();
        this.growthPhase = plantState.getGrowthPhase();
        this.maturity = plantState.getMaturity();
        this.productivity = plantState.getProductivity();
        this.decay = plantState.getDecay();
    }

    public PlantState toEntity() {
        return new PlantState(
                PlantType.getPlantType(Material.getMaterial(seedMaterial)),
                null,
                new Location(Bukkit.getWorld(worldId), x, y, z),
                plantingTick,
                updateTicks,
                growthPhase,
                maturity,
                productivity,
                decay
        );
    }
}
