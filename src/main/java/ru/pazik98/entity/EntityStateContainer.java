package ru.pazik98.entity;

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

import java.util.*;
import java.util.stream.Collectors;

public class EntityStateContainer {
    private static EntityStateContainer instance;

    private final PlantStateDataRepository plantRepository = NDatabasePlantRepository.getInstance();
    private final SoilStateDataRepository soilRepository = NDatabaseSoilRepository.getInstance();

    private final HashSet<Soil> soils = new HashSet<>();
    private final HashSet<Plant> plants = new HashSet<>();

    public Soil createSoil(Block block) {
        SoilState soil = SoilState.builder()
                .humidity((float) block.getHumidity())
                .temperature((float) block.getTemperature())
                .location(block.getLocation())
                .build();
        soils.add(soil);
        save(soil);
        return soil;
    }

    public void destroySoil(Location location) {
        Soil soil = getSoil(location);
        soils.remove(soil);
        delete(soil);
        destroyPlant(soil.getPlant());
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
        return plant;
    }

    public void destroyPlant(Location location) {
        Plant plant = getPlant(location);
        destroyPlant(plant);
    }

    private void destroyPlant(Plant plant) {
        plants.remove(plant);
        delete(plant);
        plant.getSoil().setPlant(null);
    }

    public Plant getPlant(Location location) {
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
        soilRepository.delete((SoilStateData) soil);
    }

    private void delete(Plant plant) {
        plantRepository.delete((PlantStateData) plant);
    }

    public boolean isSoil(Location location) {
        return getSoil(location) != null;
    }

    public boolean isPlant(Location location) {
        return getPlant(location) != null;
    }

    public static EntityStateContainer getInstance() {
        if (instance == null) instance = new EntityStateContainer();
        return instance;
    }
}
