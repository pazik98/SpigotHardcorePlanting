package ru.pazik98.entity;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import ru.pazik98.util.Convert;
import ru.pazik98.util.GrowthBonus;
import ru.pazik98.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class PlantState {

    private PlantType plantType;
    private long plantingTick;
    private long updatesTickNumber;
    private int growthPhase;
    private float maturity;
    private float productivity;
    private float decay;
    private Location location;

    private SoilState soil;
    private boolean isDead;

    private final Logger logger = Bukkit.getLogger();

    public PlantState(PlantType plantType, SoilState soil, Location location, long plantingTick) {
        this.plantType = plantType;
        this.plantingTick = plantingTick;
        this.updatesTickNumber = 0;
        this.growthPhase = 1;
        this.maturity = 0f;
        this.productivity = 0f;
        this.decay = 0f;
        this.soil = soil;
        this.location = location;
        this.isDead = false;
    }

    public void update() {
        // Growth phase is maximum?
        if (growthPhase == getPlantType().getGrowthStageCount()) {
            //...
        } else {
            // calculating deviation
            float humidityDiff = getPlantType().getExpectedHumidity() - Convert.humidityToPercent(getSoil().getHumidity());
            float temperatureDiff = getPlantType().getExpectedTemperature() - Convert.temperatureToDegrees(getSoil().getTemperature());

            // calculating grow chance bonuses
            float growChanceHumidityBonus = GrowthBonus.GROWTH_SPEED.getHumidityExcess() * humidityDiff;
            if (humidityDiff < 0) growChanceHumidityBonus = GrowthBonus.GROWTH_SPEED.getHumidityDeficit() * humidityDiff;

            float growChanceTemperatureBonus = GrowthBonus.GROWTH_SPEED.getTemperatureExcess() * temperatureDiff;
            if (temperatureDiff < 0) growChanceTemperatureBonus = GrowthBonus.GROWTH_SPEED.getTemperatureDeficit() * temperatureDiff;

            float growChanceFertilizerBonus = 0;
            if (soil.getFertilizer() > plantType.getGrowthFertilizerCost()) growChanceFertilizerBonus = GrowthBonus.GROWTH_SPEED.getFertilizer();

            float growChance = (growChanceHumidityBonus + growChanceTemperatureBonus + growChanceFertilizerBonus) / plantType.getGrowthTicksCost();
            logger.warning("grow chance: " + growChance);

            // Plant is trying to grow?
            if (Util.getRandom(growChance)) {
                logger.warning("trying to grow");
                //calculating death chance bonus
                float deathChanceHumidityBonus = GrowthBonus.DEATH_CHANCE.getHumidityExcess() * humidityDiff;
                if (humidityDiff < 0) deathChanceHumidityBonus = GrowthBonus.DEATH_CHANCE.getHumidityDeficit() * humidityDiff;

                float deathChanceTemperatureBonus = GrowthBonus.DEATH_CHANCE.getTemperatureExcess() * temperatureDiff;
                if (temperatureDiff < 0) deathChanceTemperatureBonus = GrowthBonus.DEATH_CHANCE.getTemperatureDeficit() * temperatureDiff;

                float deathChance = deathChanceHumidityBonus + deathChanceTemperatureBonus;
                logger.warning("death chance: " + deathChance);

                // plant died?
                if (Util.getRandom(deathChance)) {
                    logger.warning("died");
                    die();
                } else {
                    logger.warning("grow");
                    grow();
                }
            }
        }
    }

    private void grow() {
        // Check for needed resources
        if (getSoil().getWater() < getPlantType().getGrowthWaterCost()) {
            return;
        }

        // consume resources
        getSoil().decreaseWater(getPlantType().getGrowthWaterCost());
        if (soil.getFertilizer() >= plantType.getGrowthFertilizerCost()) {
            soil.decreaseFertilizer(plantType.getGrowthFertilizerCost());
        }

        // change age
        Ageable ageable = (Ageable) this.getLocation().getBlock().getBlockData();
        if (ageable.getAge() != ageable.getMaximumAge()) {
            ageable.setAge(ageable.getAge() + 1);
            this.getLocation().getBlock().setBlockData(ageable);
            growthPhase++;
        }
    }

    private void die() {
        isDead = true;
    }

    public void incrementUpdatesTickNumber() {
        updatesTickNumber++;
    }

    public void incrementUpdatesTickNumber(int ticks) {
        updatesTickNumber += ticks;
    }

    public long getUpdatesTickNumber() {
        return updatesTickNumber;
    }

    public float getHappiness() {
        float happiness = 100.0f;
        // light reason
        float light = 1f;
        if (getLocation().getBlock().getLightFromSky() < getPlantType().getExpectedLight()) light = 0.0f;
        // water reason
        float water = 1.0f - Math.abs(getPlantType().getExpectedHumidity() / 100 - getSoil().getHumidity());
        // temperature reason
        float temperature = 1.0f - Math.abs(Convert.degreesToTemperature(getPlantType().getExpectedTemperature()) - getSoil().getTemperature()) / 4;
        System.out.println("--happiness: " + light + " " + water + " " + temperature);
        return (happiness + water + temperature) / 3;
    }

    public Location getLocation() {
        return location;
    }

    public SoilState getSoil() {
        return soil;
    }

    public List<ItemStack> getCrops() {
        List<ItemStack> harvest = new ArrayList<>();
        int seedCount = Util.getRandomRound(plantType.getSeedCount() * productivity);
        int cropCount = Util.getRandomRound(plantType.getExpectedHarvest() * productivity);
        ItemStack seeds = new ItemStack(plantType.getSeedMaterial(), seedCount);
        ItemStack crop = new ItemStack(plantType.getPlantMaterial(), cropCount);
        harvest.add(seeds);
        harvest.add(crop);
        return harvest;
    }

    public PlantType getPlantType() {
        return plantType;
    }

    public long getPlantingTick() {
        return plantingTick;
    }

    public int getGrowthPhase() {
        return growthPhase;
    }

    public float getMaturity() {
        return maturity;
    }

    public float getProductivity() {
        return productivity;
    }

    public float getDecay() {
        return decay;
    }

    public boolean isDead() {
        return isDead;
    }

    @Override
    public String toString() {
        return "PlantState{" +
                "plantType=" + plantType +
                ", plantingTick=" + plantingTick +
                ", growthPhase=" + growthPhase +
                ", maturity=" + maturity +
                ", productivity=" + productivity +
                ", decay=" + decay +
                ", location=" + location +
                ", soil=" + soil +
                ", isDead=" + isDead +
                '}';
    }
}
