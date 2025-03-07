package ru.pazik98.entity.plant;

import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.data.Ageable;
import org.bukkit.inventory.ItemStack;
import ru.pazik98.db.repository.data.PlantStateData;
import ru.pazik98.entity.soil.Soil;
import ru.pazik98.util.Convert;
import ru.pazik98.util.GrowthBonus;
import ru.pazik98.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@Builder
@AllArgsConstructor
@ToString
public class PlantState implements Plant {

    @Getter
    private PlantPreset plantPreset;

    @Getter
    private long plantingTick;

    @Getter
    private long updatesTickNumber;

    @Getter
    private int growthPhase;

    @Getter
    private float maturity;

    @Getter
    private float productivity;

    @Getter
    private float decay;

    @Getter
    private Location location;

    @Getter
    @Setter
    @ToString.Exclude
    private Soil soil;

    private boolean isDead;

    private final Logger logger = Bukkit.getLogger();

    public PlantState(PlantStateData plantStateData) {
        this.plantPreset = PlantPresetManager.getPresetBySeed(Material.getMaterial(plantStateData.getSeedMaterial()));
        this.plantingTick = plantStateData.getPlantingTick();
        this.updatesTickNumber = plantStateData.getUpdateTicks();
        this.growthPhase = plantStateData.getGrowthPhase();
        this.maturity = plantStateData.getMaturity();
        this.productivity = plantStateData.getProductivity();
        this.decay = plantStateData.getDecay();
        this.location = new Location(
                Bukkit.getWorld(plantStateData.getWorldUID()),
                plantStateData.getX(),
                plantStateData.getY(),
                plantStateData.getZ()
        );
    }

    @Override
    public void update() {
        // calculating deviation
        float humidityDiff = getPlantPreset().getExpectedHumidity() - Convert.humidityToPercent(getSoil().getHumidity());
        float temperatureDiff = getPlantPreset().getExpectedTemperature() - Convert.temperatureToDegrees(getSoil().getTemperature());

        // Growth phase is maximum?
        if (growthPhase >= plantPreset.getGrowthStageCount() - 1) {
            // Maturation phase is maximum?
            if (growthPhase >= plantPreset.getGrowthStageCount() + plantPreset.getMaturationStageCount() - 1) {
                // calculating decaying chance bonus
                float decayChanceHumidityBonus = GrowthBonus.DECAY_SPEED.getHumidityExcess() * humidityDiff;
                if (humidityDiff < 0) decayChanceHumidityBonus = GrowthBonus.DECAY_SPEED.getHumidityDeficit() * humidityDiff;

                float decayChanceTemperatureBonus = GrowthBonus.DECAY_SPEED.getTemperatureExcess() * temperatureDiff;
                if (temperatureDiff < 0) decayChanceTemperatureBonus = GrowthBonus.DECAY_SPEED.getTemperatureDeficit();

                float decayChance = (decayChanceHumidityBonus + decayChanceTemperatureBonus) / plantPreset.getDecayTicksCost();
                if (Util.getRandom(decayChance)) {
                    decay();
                }
            } else {
                // calculating mature chance bonuses
                float matureChanceTemperatureBonus = GrowthBonus.MATURITY_SPEED.getTemperatureExcess() * temperatureDiff;
                if (temperatureDiff < 0) matureChanceTemperatureBonus = GrowthBonus.MATURITY_SPEED.getTemperatureDeficit() * temperatureDiff;

                float matureChanceFertilizerBonus = 0;
                if (soil.getFertilizer() >= plantPreset.getMaturationFertilizerCost()) matureChanceFertilizerBonus = GrowthBonus.MATURITY_SPEED.getFertilizer();

                float matureChance = (matureChanceTemperatureBonus + matureChanceFertilizerBonus) / plantPreset.getMaturationTicksCost();
                if (Util.getRandom(matureChance)) {
                    mature();
                }
            }
        } else {
            // calculating grow chance bonuses

            float growChanceTemperatureBonus = GrowthBonus.GROWTH_SPEED.getTemperatureExcess() * temperatureDiff;
            if (temperatureDiff < 0) growChanceTemperatureBonus = GrowthBonus.GROWTH_SPEED.getTemperatureDeficit() * temperatureDiff;

            float growChanceFertilizerBonus = 0;
            if (soil.getFertilizer() >= plantPreset.getGrowthFertilizerCost()) growChanceFertilizerBonus = GrowthBonus.GROWTH_SPEED.getFertilizer();

            float growChance = (growChanceTemperatureBonus + growChanceFertilizerBonus) / plantPreset.getGrowthTicksCost();

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

    @Override
    public void grow() {
        // Check for needed resources
        if (getSoil().getWater() < getPlantPreset().getGrowthWaterCost()) {
            return;
        }

        // consume resources
        getSoil().decreaseWater(getPlantPreset().getGrowthWaterCost());
        if (soil.getFertilizer() >= plantPreset.getGrowthFertilizerCost()) {
            soil.decreaseFertilizer(plantPreset.getGrowthFertilizerCost());
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

    @Override
    public void mature() {
        // check for needed resources
        if (soil.getWater() < plantPreset.getMaturationWaterCost()) {
            return;
        }
        // consume
        float productivityFertilizerBonus = 0;

        soil.decreaseWater(plantPreset.getMaturationWaterCost());
        if (soil.getFertilizer() >= plantPreset.getMaturationFertilizerCost()) {
            soil.decreaseFertilizer(plantPreset.getMaturationFertilizerCost());
            productivityFertilizerBonus = GrowthBonus.CROP_AMOUNT.getFertilizer();
        }

        // change maturity
        growthPhase++;
        maturity = (float) (growthPhase + 1 - plantPreset.getGrowthStageCount()) / plantPreset.getMaturationStageCount();

        // change productivity
        float humidityDiff = soil.getHumidity() - plantPreset.getExpectedHumidity();
        float productivityHumidityBonus = GrowthBonus.CROP_AMOUNT.getHumidityExcess() * humidityDiff;
        if (humidityDiff < 0) productivityHumidityBonus = GrowthBonus.CROP_AMOUNT.getHumidityDeficit() * humidityDiff;
        productivity += ((float) (growthPhase + 1 - plantPreset.getGrowthStageCount()) / plantPreset.getMaturationStageCount() *
                (productivityHumidityBonus + productivityFertilizerBonus));

        logger.warning("maturing " + this);
    }

    @Override
    public void die() {
        isDead = true;
    }

    @Override
    public void decay() {
        // consume resources
        if (soil.getWater() >= plantPreset.getDecayWaterCost()) {
            soil.decreaseWater(plantPreset.getDecayWaterCost());
        }
        if (soil.getFertilizer() >= plantPreset.getDecayFertilizerCost()) {
            soil.decreaseFertilizer(plantPreset.getDecayFertilizerCost());
        }

        //change productivity
        growthPhase++;
        decay = (float) (growthPhase + 1 - plantPreset.getGrowthStageCount() - plantPreset.getMaturationStageCount()) / plantPreset.getDecayStageCount();

        float loss = (float) ((productivity / (plantPreset.getDecayStageCount() - (growthPhase + 1 - plantPreset.getGrowthStageCount() - plantPreset.getMaturationStageCount()))) - 0.01);
        if (loss > productivity) productivity = 0;
        else productivity -= loss;

        if (decay >= 1) die();
        logger.warning(String.valueOf(plantPreset.getDecayStageCount() - (growthPhase + 1 - plantPreset.getGrowthStageCount() - plantPreset.getMaturationStageCount())));
        logger.warning("decaying " + this);
    }

    @Override
    public void incrementUpdatesTickNumber() {
        updatesTickNumber++;
    }

    public void incrementUpdatesTickNumber(int ticks) {
        updatesTickNumber += ticks;
    }

    @Override
    public float getHappiness() {
        float happiness = 100.0f;
        // light reason
        float light = 1f;
        if (getLocation().getBlock().getLightFromSky() < getPlantPreset().getExpectedLight()) light = 0.0f;
        // water reason
        float water = 1.0f - Math.abs(getPlantPreset().getExpectedHumidity() / 100 - getSoil().getHumidity());
        // temperature reason
        float temperature = 1.0f - Math.abs(Convert.degreesToTemperature(getPlantPreset().getExpectedTemperature()) - getSoil().getTemperature()) / 4;
        return (happiness + water + temperature) / 3;
    }

    @Override
    public List<ItemStack> getCrops() {
        List<ItemStack> harvest = new ArrayList<>();
        int seedCount = Util.getRandomRound(plantPreset.getExpectedSeedCount() * productivity);
        int cropCount = Util.getRandomRound(plantPreset.getExpectedHarvestCount() * productivity);
        ItemStack seeds = new ItemStack(plantPreset.getSeedMaterial(), seedCount);
        ItemStack crop = new ItemStack(plantPreset.getHarvestMaterial(), cropCount);
        harvest.add(seeds);
        harvest.add(crop);
        return harvest;
    }

    @Override
    public boolean isDead() {
        return isDead;
    }

    public static PlantState from(PlantStateData plantStateData) {
        return new PlantState(plantStateData);
    }
}
