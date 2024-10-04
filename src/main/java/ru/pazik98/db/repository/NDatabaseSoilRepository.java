package ru.pazik98.db.repository;

import com.nivixx.ndatabase.api.NDatabase;
import com.nivixx.ndatabase.api.query.NQuery;
import com.nivixx.ndatabase.api.repository.Repository;
import org.bukkit.Chunk;
import ru.pazik98.db.repository.data.SoilStateData;

import java.util.List;
import java.util.UUID;

public class NDatabaseSoilRepository implements SoilStateDataRepository {

    private static NDatabaseSoilRepository instance;
    private final Repository<Integer, SoilStateData> repository = NDatabase.api().getOrCreateRepository(SoilStateData.class);

    private NDatabaseSoilRepository() {};

    @Override
    public List<SoilStateData> findAll(Chunk chunk) {
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
    public void save(SoilStateData soilStateData) {
        repository.upsert(soilStateData);
    }

    @Override
    public void delete(SoilStateData soilStateData) {
        repository.delete(soilStateData);
    }

    public static NDatabaseSoilRepository getInstance() {
        if (instance == null) instance = new NDatabaseSoilRepository();
        return instance;
    }
}
