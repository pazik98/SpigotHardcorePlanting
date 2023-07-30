package ru.pazik98.entity;

import org.bukkit.Material;

public enum PlantType {

    WHEAT (Material.WHEAT, Material.WHEAT_SEEDS, 2, 7, 8, 10,
            15, 38, 17, 3, 5, 3, 12000,
            1, 10, 10, 1, 5, 0, 10);


    private Material plantMaterial;
    private Material seedMaterial;
    private float seedCount;

    // Required number of growth iterations for each phase
    private int growthStageCount;
    private int maturationStageCount;
    private int decayStageCount;

    // Expected environmental performance for ideal growth
    private int expectedLight;
    private float expectedHumidity;
    private float expectedTemperature;

    // Harvest count
    private float expectedHarvest;

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

    PlantType(Material plantMaterial, Material seedMaterial, float seedCount, int growthStageCount, int maturationStageCount, int decayStageCount, int expectedLight, float expectedHumidity, float expectedTemperature, float expectedHarvest, float fertilizerRatio, float growthWaterCost, float growthFertilizerCost, float growthTicksCost, float maturationWaterCost, float maturationFertilizerCost, float maturationTicksCost, float decayWaterCost, float decayFertilizerCost, float decayTicksCost) {
        this.plantMaterial = plantMaterial;
        this.seedMaterial = seedMaterial;
        this.seedCount = seedCount;
        this.growthStageCount = growthStageCount;
        this.maturationStageCount = maturationStageCount;
        this.decayStageCount = decayStageCount;
        this.expectedLight = expectedLight;
        this.expectedHumidity = expectedHumidity;
        this.expectedTemperature = expectedTemperature;
        this.expectedHarvest = expectedHarvest;
        this.growthWaterCost = growthWaterCost;
        this.growthFertilizerCost = growthFertilizerCost;
        this.growthTicksCost = growthTicksCost;
        this.maturationWaterCost = maturationWaterCost;
        this.maturationFertilizerCost = maturationFertilizerCost;
        this.maturationTicksCost = maturationTicksCost;
        this.decayWaterCost = decayWaterCost;
        this.decayFertilizerCost = decayFertilizerCost;
        this.decayTicksCost = decayTicksCost;
    }

    public Material getPlantMaterial() {
        return plantMaterial;
    }

    public Material getSeedMaterial() {
        return seedMaterial;
    }

    public float getSeedCount() {
        return seedCount;
    }

    public int getGrowthStageCount() {
        return growthStageCount;
    }

    public int getMaturationStageCount() {
        return maturationStageCount;
    }

    public int getDecayStageCount() {
        return decayStageCount;
    }

    public int getExpectedLight() {
        return expectedLight;
    }

    public float getExpectedHumidity() {
        return expectedHumidity;
    }

    public float getExpectedTemperature() {
        return expectedTemperature;
    }

    public float getExpectedHarvest() {
        return expectedHarvest;
    }

    public float getGrowthWaterCost() {
        return growthWaterCost;
    }

    public float getGrowthFertilizerCost() {
        return growthFertilizerCost;
    }

    public float getGrowthTicksCost() {
        return growthTicksCost;
    }

    public float getMaturationWaterCost() {
        return maturationWaterCost;
    }

    public float getMaturationFertilizerCost() {
        return maturationFertilizerCost;
    }

    public float getMaturationTicksCost() {
        return maturationTicksCost;
    }

    public float getDecayWaterCost() {
        return decayWaterCost;
    }

    public float getDecayFertilizerCost() {
        return decayFertilizerCost;
    }

    public float getDecayTicksCost() {
        return decayTicksCost;
    }


    // Method for finding plant type by its seeds
    // Conceived for use in planting moment
    public static PlantType getPlantType(Material seedMaterial) {
        for (PlantType plantType : PlantType.values()) {
            if (seedMaterial.equals(plantType.seedMaterial)) return plantType;
        }
        return null;
    }

    public static boolean isPlant(Material blockMaterial) {
        for (PlantType plantType : PlantType.values()) {
            if (blockMaterial.equals(plantType.plantMaterial)) return true;
        }
        return false;
    }
}
