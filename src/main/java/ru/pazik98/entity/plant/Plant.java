package ru.pazik98.entity.plant;

import org.bukkit.inventory.ItemStack;
import ru.pazik98.entity.soil.Soil;

import java.util.List;

public interface Plant {
    void update();

    void grow();

    void die();

    void mature();

    void decay();

    void incrementUpdatesTickNumber();

    float getHappiness();

    List<ItemStack> getCrops();

    void setSoil(Soil soil);

    boolean isDead();

    PlantType getPlantType();

    long getPlantingTick();

    long getUpdatesTickNumber();

    int getGrowthPhase();

    float getMaturity();

    float getProductivity();

    float getDecay();

    org.bukkit.Location getLocation();

    Soil getSoil();
}
