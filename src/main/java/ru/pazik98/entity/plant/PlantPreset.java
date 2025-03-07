package ru.pazik98.entity.plant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Material;

@Getter
@Builder
@ToString
public class PlantPreset {

    private String name;

    private Material blockMaterial;
    private Material seedMaterial;
    private Material harvestMaterial;

    // Required number of growth iterations for each phase
    private int growthStageCount;
    private int maturationStageCount;
    private int decayStageCount;

    // Expected environmental performance for ideal growth
    private int expectedLight;
    private float expectedHumidity;
    private float expectedTemperature;

    // Harvest count
    private float expectedHarvestCount;
    private float expectedSeedCount;

    // Consumption per growth stage
    private float growthWaterCost;
    private float growthFertilizerCost;
    private float growthTicksCost;

    // Consumption per maturation stage
    private float maturationWaterCost;
    private float maturationFertilizerCost;
    private float maturationTicksCost;

    // Consumption per decay stage
    private float decayWaterCost;
    private float decayFertilizerCost;
    private float decayTicksCost;
}
