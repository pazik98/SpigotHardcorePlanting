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
        this.growthPhase = 0;
        this.maturity = 0f;
        this.productivity = 0f;
        this.decay = 0f;
        this.soil = soil;
        this.location = location;
        this.isDead = false;
    }

    public PlantState(PlantType plantType, SoilState soil, Location location, long plantingTick, long updatesTickNumber,
                      int growthPhase, float maturity, float productivity, float decay) {
        this.plantType = plantType;
        this.plantingTick = plantingTick;
        this.updatesTickNumber = updatesTickNumber;
        this.growthPhase = growthPhase;
        this.maturity = maturity;
        this.productivity = productivity;
        this.decay = decay;
        this.soil = soil;
        this.location = location;
        this.isDead = false;
    }

    public PlantState(PlantType plantType, Location location, long plantingTick, long updatesTickNumber,
                      int growthPhase, float maturity, float productivity, float decay) {
        this.plantType = plantType;
        this.plantingTick = plantingTick;
        this.updatesTickNumber = updatesTickNumber;
        this.growthPhase = growthPhase;
        this.maturity = maturity;
        this.productivity = productivity;
        this.decay = decay;
        this.soil = null;
        this.location = location;
        this.isDead = false;
    }

    public void update() {
        // calculating deviation
        float humidityDiff = getPlantType().getExpectedHumidity() - Convert.humidityToPercent(getSoil().getHumidity());
        float temperatureDiff = getPlantType().getExpectedTemperature() - Convert.temperatureToDegrees(getSoil().getTemperature());

        // Growth phase is maximum?
        if (growthPhase >= plantType.getGrowthStageCount() - 1) {
            // Maturation phase is maximum?
            if (growthPhase >= plantType.getGrowthStageCount() + plantType.getMaturationStageCount() - 1) {
                // calculating decaying chance bonus
                float decayChanceHumidityBonus = GrowthBonus.DECAY_SPEED.getHumidityExcess() * humidityDiff;
                if (humidityDiff < 0) decayChanceHumidityBonus = GrowthBonus.DECAY_SPEED.getHumidityDeficit() * humidityDiff;

                float decayChanceTemperatureBonus = GrowthBonus.DECAY_SPEED.getTemperatureExcess() * temperatureDiff;
                if (temperatureDiff < 0) decayChanceTemperatureBonus = GrowthBonus.DECAY_SPEED.getTemperatureDeficit();

                float decayChance = (decayChanceHumidityBonus + decayChanceTemperatureBonus) / plantType.getDecayTicksCost();
                if (Util.getRandom(decayChance)) {
                    decay();
                }
            } else {
                // calculating mature chance bonuses
                float matureChanceTemperatureBonus = GrowthBonus.MATURITY_SPEED.getTemperatureExcess() * temperatureDiff;
                if (temperatureDiff < 0) matureChanceTemperatureBonus = GrowthBonus.MATURITY_SPEED.getTemperatureExcess() * temperatureDiff;

                float matureChanceFertilizerBonus = 0;
                if (soil.getFertilizer() >= plantType.getMaturationFertilizerCost()) matureChanceFertilizerBonus = GrowthBonus.MATURITY_SPEED.getFertilizer();

                float matureChance = (matureChanceTemperatureBonus + matureChanceFertilizerBonus) / plantType.getMaturationTicksCost();
                if (Util.getRandom(matureChance)) {
                    mature();
                }
            }
        } else {
            // calculating grow chance bonuses

            float growChanceTemperatureBonus = GrowthBonus.GROWTH_SPEED.getTemperatureExcess() * temperatureDiff;
            if (temperatureDiff < 0) growChanceTemperatureBonus = GrowthBonus.GROWTH_SPEED.getTemperatureDeficit() * temperatureDiff;

            float growChanceFertilizerBonus = 0;
            if (soil.getFertilizer() >= plantType.getGrowthFertilizerCost()) growChanceFertilizerBonus = GrowthBonus.GROWTH_SPEED.getFertilizer();

            float growChance = (growChanceTemperatureBonus + growChanceFertilizerBonus) / plantType.getGrowthTicksCost();

            // Plant is trying to grow?
            if (Util.getRandom(growChance)) {
                //calculating death chance bonus
                float deathChanceHumidityBonus = GrowthBonus.DEATH_CHANCE.getHumidityExcess() * humidityDiff;
                if (humidityDiff < 0) deathChanceHumidityBonus = GrowthBonus.DEATH_CHANCE.getHumidityDeficit() * humidityDiff;

                float deathChanceTemperatureBonus = GrowthBonus.DEATH_CHANCE.getTemperatureExcess() * temperatureDiff;
                if (temperatureDiff < 0) deathChanceTemperatureBonus = GrowthBonus.DEATH_CHANCE.getTemperatureDeficit() * temperatureDiff;

                float deathChance = deathChanceHumidityBonus + deathChanceTemperatureBonus;

                // plant died?
                if (Util.getRandom(deathChance)) {
                    die();
                } else {
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

        logger.warning("growing " + this);
    }

    private void die() {
        isDead = true;
    }

    private void mature() {
        // check for needed resources
        if (soil.getWater() < plantType.getMaturationWaterCost()) {
            return;
        }
        // consume
        float productivityFertilizerBonus = 0;

        soil.decreaseWater(plantType.getMaturationWaterCost());
        if (soil.getFertilizer() >= plantType.getMaturationFertilizerCost()) {
            soil.decreaseFertilizer(plantType.getMaturationFertilizerCost());
            productivityFertilizerBonus = GrowthBonus.CROP_AMOUNT.getFertilizer();
        }

        // change maturity
        growthPhase++;
        maturity = (float) (growthPhase + 1 - plantType.getGrowthStageCount()) / plantType.getMaturationStageCount();

        // change productivity
        float humidityDiff = soil.getHumidity() - plantType.getExpectedHumidity();
        float productivityHumidityBonus = GrowthBonus.CROP_AMOUNT.getHumidityExcess() * humidityDiff;
        if (humidityDiff < 0) productivityHumidityBonus = GrowthBonus.CROP_AMOUNT.getHumidityDeficit() * humidityDiff;
        productivity += ((float)(growthPhase + 1 - plantType.getGrowthStageCount()) / plantType.getMaturationStageCount() *
                (productivityHumidityBonus + productivityFertilizerBonus));

        logger.warning("maturing " + this);
    }

    private void decay() {
        // consume resources
        if (soil.getWater() >= plantType.getDecayWaterCost()) {
            soil.decreaseWater(plantType.getDecayWaterCost());
        }
        if (soil.getFertilizer() >= plantType.getDecayFertilizerCost()) {
            soil.decreaseFertilizer(plantType.getDecayFertilizerCost());
        }

        //change productivity
        growthPhase++;
        decay = (float) (growthPhase + 1 - plantType.getGrowthStageCount() - plantType.getMaturationStageCount()) / plantType.getDecayStageCount();

        float loss = (float) (( productivity / (plantType.getDecayStageCount() - (growthPhase + 1 - plantType.getGrowthStageCount() - plantType.getMaturationStageCount()))) - 0.01);
        if (loss > productivity) productivity = 0;
        else productivity -= loss;

        if (decay >= 1) die();
        logger.warning(String.valueOf(plantType.getDecayStageCount() - (growthPhase + 1 - plantType.getGrowthStageCount() - plantType.getMaturationStageCount())));
        logger.warning("decaying " + this);
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

    public void setSoil(SoilState soil) {
        this.soil = soil;
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
