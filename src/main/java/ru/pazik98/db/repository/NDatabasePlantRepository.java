package ru.pazik98.db.repository;

import com.nivixx.ndatabase.api.NDatabase;
import com.nivixx.ndatabase.api.query.NQuery;
import com.nivixx.ndatabase.api.repository.Repository;
import org.bukkit.Chunk;
import ru.pazik98.db.repository.data.PlantStateData;

import java.util.List;
import java.util.UUID;

public class NDatabasePlantRepository implements PlantStateDataRepository {
    private static NDatabasePlantRepository instance;
    private final Repository<Integer, PlantStateData> repository = NDatabase.api().getOrCreateRepository(PlantStateData.class);

    private NDatabasePlantRepository() { }
    @Override
    public List<PlantStateData> findAll(Chunk chunk) {
        int xMin = chunk.getX() * 16;
        int xMax = xMin + 16;
        int zMin = chunk.getZ() * 16;
        int zMax = zMin + 16;
        UUID worldUID = chunk.getWorld().getUID();
        return repository.find(NQuery.predicate(
                "$.world_uid == %s && $.x >= %d && $.x < %d && $.z >= %d && $.z < %d"
                        .formatted(worldUID, xMin, xMax, zMin, zMax))
        );
    }

    @Override
    public void save(PlantStateData plantStateData) {
        repository.upsert(plantStateData);
    }

    @Override
    public void delete(PlantStateData plantStateData) {
        repository.delete(plantStateData);
    }

    public static NDatabasePlantRepository getInstance() {
        if (instance == null) {
            instance = new NDatabasePlantRepository();
        }
        return instance;
    }
}
