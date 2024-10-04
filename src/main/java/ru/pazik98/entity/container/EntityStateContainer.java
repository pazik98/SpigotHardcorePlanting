package ru.pazik98.entity.container;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import ru.pazik98.db.repository.NDatabasePlantRepository;
import ru.pazik98.db.repository.PlantStateDataRepository;
import ru.pazik98.db.repository.SoilStateDataRepository;
import ru.pazik98.db.repository.NDatabaseSoilRepository;
import ru.pazik98.db.repository.data.PlantStateData;
import ru.pazik98.db.repository.data.SoilStateData;
import ru.pazik98.entity.plant.Plant;
import ru.pazik98.entity.plant.PlantState;
import ru.pazik98.entity.plant.PlantType;
import ru.pazik98.entity.soil.Soil;
import ru.pazik98.entity.soil.SoilState;
import ru.pazik98.plugin.HardcorePlanting;
import ru.pazik98.util.Util;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class EntityStateContainer {
    private static EntityStateContainer instance;
    private final Logger logger = HardcorePlanting.getInstance().getLogger();

    // TEMPORARY
    private final float tickFrequency = 0.5f;

    private final PlantStateDataRepository plantRepository = NDatabasePlantRepository.getInstance();
    private final SoilStateDataRepository soilRepository = NDatabaseSoilRepository.getInstance();

    private final HashSet<Soil> soils = new HashSet<>();
    private final HashSet<Plant> plants = new HashSet<>();

    public Soil createSoil(Block block) {
        SoilState soil = SoilState.builder()
                .humidity((float) block.getHumidity())
                .temperature((float) block.getTemperature())
                .waterCapacity(1000)
                .water(500)
                .location(block.getLocation())
                .build();
        soils.add(soil);
        save(soil);
        logger.warning("Created soil: " + soil);
        return soil;
    }

    public void destroySoil(Location location) {
        Soil soil = getSoil(location);
        soils.remove(soil);
        delete(soil);
        logger.warning("Destroyed soil: " + soil);
        if (soil.getPlant() != null) destroyPlant(soil.getPlant());

    }

    public Soil getSoil(Location location) {
        Optional<Soil> matchedSoil = soils.stream()
                .parallel()
                .filter(x -> x.getLocation().equals(location))
                .findFirst();
        return matchedSoil.orElse(null);
    }

    public Plant createPlant(Soil soil, Location location, Material seedMaterial) {
        PlantState plant = PlantState.builder()
                .plantType(PlantType.getPlantType(seedMaterial))
                .plantingTick(location.getWorld().getFullTime())
                .location(location)
                .soil(soil)
                .build();
        soil.setPlant(plant);
        plants.add(plant);
        plantRepository.save(new PlantStateData(plant));
        logger.warning("Created plant: " + plant);
        return plant;
    }

    public void destroyPlant(Location location) {
        Plant plant = getPlant(location);
        destroyPlant(plant);
        logger.warning("Destroyed plant: " + plant);
    }

    private void destroyPlant(Plant plant) {
        plants.remove(plant);
        delete(plant);
        plant.getSoil().setPlant(null);
    }

    public Plant getPlant(Location location) {
        logger.warning(plants.toString());
        Optional<Plant> matchedPlant = plants.stream()
                .parallel()
                .filter(x -> x.getLocation().equals(location))
                .findFirst();
        return matchedPlant.orElse(null);
    }

    public void load(Chunk chunk) {
        List<SoilState> soilStates = soilRepository.findAll(chunk).stream()
                .map(SoilState::from)
                .toList();

        List<PlantState> plantStates = soilStates.stream()
                .map(SoilState::getPlant)
                .map(PlantState.class::cast)
                .filter(Objects::nonNull)
                .toList();

        soils.addAll(soilStates);
        plants.addAll(plantStates);

    }

    public void unload(Chunk chunk) {
        Set<Soil> soilsAtChunk = soils.stream()
                .filter(x -> x.getLocation().getChunk().equals(chunk))
                .collect(Collectors.toSet());

        Set<Plant> plantsAtChunk = soilsAtChunk.stream()
                .map(Soil::getPlant)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        soils.removeAll(soilsAtChunk);
        plants.removeAll(plantsAtChunk);

        soilsAtChunk.forEach(this::save);
        plantsAtChunk.forEach(this::save);
    }

    private void save(Soil soil) {
        soilRepository.save(new SoilStateData((SoilState) soil));
    }

    private void save(Plant plant) {
        plantRepository.save(new PlantStateData((PlantState) plant));
    }

    private void delete(Soil soil) {
        soilRepository.delete(new SoilStateData((SoilState) soil));
    }

    private void delete(Plant plant) {
        plantRepository.delete(new PlantStateData((PlantState) plant));
    }

    public boolean isSoil(Location location) {
        return getSoil(location) != null;
    }

    public boolean isPlant(Location location) {
        return getPlant(location) != null;
    }

    public void update() {
        plants.forEach(this::update);
    }

    private void update(Plant plant) {
        if (!plant.getPlantType().getPlantMaterial().equals(plant.getLocation().getBlock().getType())) {
            destroyPlant(plant.getLocation());
            return;
        }
        // Check for death
        if (plant.isDead()) {
            destroyPlant(plant.getLocation());
            plant.getLocation().getBlock().setType(Material.DEAD_BUSH);
            return;
        }
        plant.incrementUpdatesTickNumber();
        if (Util.getRandom(tickFrequency)) {
            plant.update();
        }
    }

    public static EntityStateContainer getInstance() {
        if (instance == null) instance = new EntityStateContainer();
        return instance;
    }
}
