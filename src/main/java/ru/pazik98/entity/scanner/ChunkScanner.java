package ru.pazik98.entity.scanner;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import ru.pazik98.db.repository.NDatabasePlantRepository;
import ru.pazik98.db.repository.NDatabaseSoilRepository;
import ru.pazik98.entity.container.EntityStateContainer;
import ru.pazik98.entity.plant.PlantPresetManager;
import ru.pazik98.entity.plant.PlantState;
import ru.pazik98.entity.scanner.error.EntityInfoError;
import ru.pazik98.entity.scanner.error.EntityInfoErrorType;
import ru.pazik98.entity.soil.SoilState;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class ChunkScanner {

    private final NDatabaseSoilRepository soilRepository = NDatabaseSoilRepository.getInstance();
    private final NDatabasePlantRepository plantRepository = NDatabasePlantRepository.getInstance();
    private final EntityStateContainer container = EntityStateContainer.getInstance();

    public Set<EntityInfoError> scanInfoErrors(Chunk chunk)
    {
        Set<Block> allBlocks = getAllBlocks(chunk);

        Set<SoilState> allSoils = soilRepository.findAll(chunk).stream()
                .map(SoilState::from)
                .collect(Collectors.toSet());

        Set<PlantState> allPlants = plantRepository.findAll(chunk).stream()
                .map(PlantState::from)
                .collect(Collectors.toSet());

        // Check for expected soil errors
        Set<EntityInfoError> expectedSoilErrors = allSoils.stream()
                .map(SoilState::getLocation)
                .filter(location -> !isFarmland(location))
                .map(location -> new EntityInfoError(location, EntityInfoErrorType.EXPECTED_SOIL))
                .collect(Collectors.toSet());

        // Check for expected plant errors
        Set<EntityInfoError> expectedPlantErrors = allPlants.stream()
                .map(PlantState::getLocation)
                .filter(location -> !isGrowable(location))
                .map(location -> new EntityInfoError(location, EntityInfoErrorType.EXPECTED_PLANT))
                .collect(Collectors.toSet());

        // Check for unexpected soil rrors
        Set<Block> allSoilBlocks = allSoils.stream()
                .map(soil -> soil.getLocation().getBlock())
                .collect(Collectors.toSet());

        Set<EntityInfoError> unexpectedSoilErrors = allBlocks.stream()
                .filter(block -> block.getBlockData().getMaterial().equals(Material.FARMLAND))
                .filter(block -> !allSoilBlocks.contains(block))
                .map(block -> new EntityInfoError(block.getLocation(), EntityInfoErrorType.UNEXPECTED_SOIL))
                .collect(Collectors.toSet());

        // Check for unexpected plant errors
        Set<Block> allPlantBlocks = allPlants.stream()
                .map(plant -> plant.getLocation().getBlock())
                .collect(Collectors.toSet());

        Set<EntityInfoError> unexpectedPlantErrors = allBlocks.stream()
                .filter(block -> PlantPresetManager.isPlant(block.getBlockData().getMaterial()))
                .filter(block -> !allPlantBlocks.contains(block))
                .map(block -> new EntityInfoError(block.getLocation(), EntityInfoErrorType.UNEXPECTED_PLANT))
                .collect(Collectors.toSet());

        Set<EntityInfoError> allErrors = new HashSet<>();
        allErrors.addAll(expectedSoilErrors);
        allErrors.addAll(expectedPlantErrors);
        allErrors.addAll(unexpectedSoilErrors);
        allErrors.addAll(unexpectedPlantErrors);
        return allErrors;
    }

    private boolean isFarmland(Location location)
    {
        return location.getBlock().getBlockData().getMaterial().equals(Material.FARMLAND);
    }

    private boolean isGrowable(Location location)
    {
        return PlantPresetManager.isPlant(location.getBlock().getBlockData().getMaterial());
    }

    private Set<Block> getAllBlocks(Chunk chunk)
    {
        Set<Block> blocks = new HashSet<>();
        for (int x = 0; x < 16; x++)
        {
            for (int z = 0; z < 16; z++)
            {
                for (int y = 0; y < 256; y++)
                {
                    blocks.add(chunk.getBlock(x, y, z));
                }
            }
        }
        return blocks;
    }
}
