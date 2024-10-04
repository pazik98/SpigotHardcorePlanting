package ru.pazik98.db.repository;

import org.bukkit.Chunk;
import ru.pazik98.db.repository.data.SoilStateData;

import java.util.List;

public interface SoilStateDataRepository {
    List<SoilStateData> findAll(Chunk chunk);

    void save(SoilStateData soilStateData);

    void delete(SoilStateData soilStateData);
}
