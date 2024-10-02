package ru.pazik98.db.repository;

import org.bukkit.Chunk;
import ru.pazik98.db.PlantStateData;

import java.util.List;

public interface PlantStateDataRepository {

    List<PlantStateData> find(Chunk chunk);

    void save(PlantStateData plantState);

    void delete(PlantStateData plantState);
}
