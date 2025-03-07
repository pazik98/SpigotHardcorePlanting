package ru.pazik98.entity.scanner;

import org.bukkit.Location;
import org.bukkit.Material;
import ru.pazik98.db.repository.NDatabasePlantRepository;
import ru.pazik98.db.repository.NDatabaseSoilRepository;
import ru.pazik98.db.repository.data.PlantStateData;
import ru.pazik98.db.repository.data.SoilStateData;
import ru.pazik98.entity.container.EntityStateContainer;
import ru.pazik98.entity.scanner.error.EntityInfoError;

public class ChunkFixer {

    private final NDatabaseSoilRepository soilRepository = NDatabaseSoilRepository.getInstance();
    private final NDatabasePlantRepository plantRepository = NDatabasePlantRepository.getInstance();
    private final EntityStateContainer container = EntityStateContainer.getInstance();

    public void fix(EntityInfoError error) {
        switch (error.getErrorType()) {
            case UNEXPECTED_PLANT:
                fixUnexpectedPlantError(error.getLocation());
                break;
            case UNEXPECTED_SOIL:
                fixUnexpectedSoilError(error.getLocation());
                break;
            case EXPECTED_PLANT:
                fixExpectedPlantError(error.getLocation());
                break;
            case EXPECTED_SOIL:
                fixExpectedSoilError(error.getLocation());
                break;
        }
    }

    private void fixUnexpectedPlantError(Location location) {
        location.getBlock().setType(Material.AIR);
    }

    private void fixUnexpectedSoilError(Location location) {
        container.createSoil(location.getBlock());
    }

    private void fixExpectedPlantError(Location location) {
        plantRepository.delete((PlantStateData) container.getPlant(location));
    }

    private void fixExpectedSoilError(Location location) {
        soilRepository.delete((SoilStateData) container.getSoil(location));
    }
}
