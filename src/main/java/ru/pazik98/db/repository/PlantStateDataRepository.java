package ru.pazik98.db.repository;

import org.bukkit.Chunk;
import ru.pazik98.db.repository.data.PlantStateData;

import java.util.List;

public interface PlantStateDataRepository {

    List<PlantStateData> findAll(Chunk chunk);

    void save(PlantStateData plantStateData);

    void delete(PlantStateData plantStateData);
}
