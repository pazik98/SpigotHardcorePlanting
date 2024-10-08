package ru.pazik98.entity.container;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
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
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class EntityStateContainer {
    private static EntityStateContainer instance;
    private final Logger logger = HardcorePlanting.getInstance().getLogger();
    private final FileConfiguration config = HardcorePlanting.getInstance().getConfig();

    private final PlantStateDataRepository plantRepository = NDatabasePlantRepository.getInstance();
    private final SoilStateDataRepository soilRepository = NDatabaseSoilRepository.getInstance();

    private final ConcurrentHashMap<Location, Soil> soils = new ConcurrentHashMap<>();
    private final  ConcurrentHashMap<Location, Plant> plants = new  ConcurrentHashMap<>();

    public Soil createSoil(Block block) {
        SoilState soil = SoilState.builder()
                .humidity((float) block.getHumidity())
                .temperature((float) block.getTemperature())
                .waterCapacity(1000)
                .water(500)
                .location(block.getLocation())
                .build();
        soils.put(block.getLocation(), soil);
        save(soil);
        logger.warning("Created soil: " + soil);
        return soil;
    }

    public void destroySoil(Location location) {
        Soil soil = getSoil(location);
        if (soil.getPlant() != null) destroyPlant(soil.getPlant());
        soils.remove(soil.getLocation());
        delete(soil);
        logger.warning("Destroyed soil: " + soil);
    }

    public Soil getSoil(Location location) {
        Optional<Soil> matchedSoil = soils.values().stream()
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
        plants.put(location, plant);
        save(plant);
        save(soil);
        logger.warning("Created plant: " + plant);
        return plant;
    }

    public void destroyPlant(Location location) {
        Plant plant = getPlant(location);
        destroyPlant(plant);
    }

    private void destroyPlant(Plant plant) {
        plants.remove(plant.getLocation());
        delete(plant);
        plant.getSoil().setPlant(null);
        logger.warning("Destroyed plant: " + plant);
    }

    public Plant getPlant(Location location) {
        Optional<Plant> matchedPlant = plants.values().stream()
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

        soilStates.stream()
                .filter(x -> x.getPlant() != null)
                .forEach(x -> x.getPlant().setSoil(x));

        soilStates.forEach(x -> soils.put(x.getLocation(), x));
        plantStates.forEach(x -> plants.put(x.getLocation(), x));
    }

    public void load(Set<Chunk> chunks) {
        chunks.forEach(this::load);
    }

    public void unload(Chunk chunk) {
        Set<Soil> soilsAtChunk = soils.values().stream()
                .filter(x -> x.getLocation().getChunk().equals(chunk))
                .collect(Collectors.toSet());

        Set<Plant> plantsAtChunk = soilsAtChunk.stream()
                .map(Soil::getPlant)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        soilsAtChunk.forEach(x -> soils.remove(x.getLocation()));
        plantsAtChunk.forEach(x -> plants.remove(x.getLocation()));

        soilsAtChunk.forEach(this::save);
        plantsAtChunk.forEach(this::save);
    }

    private void save(Soil soil) {
        soilRepository.save(new SoilStateData((SoilState) soil));
    }

    private void save(Plant plant) {
        plantRepository.save(new PlantStateData((PlantState) plant));
    }

    public void saveAll() {
        soils.values().forEach(this::save);
        plants.values().forEach(this::save);
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
        plants.values().forEach(this::update);
    }

    private void update(Plant plant) {
        if (!plant.getPlantType().getPlantMaterial().equals(plant.getLocation().getBlock().getType())) {
            destroyPlant(plant.getLocation());
            return;
        }

        if (plant.isDead()) {
            destroyPlant(plant.getLocation());
            plant.getLocation().getBlock().setType(Material.DEAD_BUSH);
            return;
        }

        plant.incrementUpdatesTickNumber();
        if (Util.getRandom((float) (1 / config.getDouble("plants-update-ticks")))) {
            plant.update();
        }
    }

    public static EntityStateContainer getInstance() {
        if (instance == null) instance = new EntityStateContainer();
        return instance;
    }
}
