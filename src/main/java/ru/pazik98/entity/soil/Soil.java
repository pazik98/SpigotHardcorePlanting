package ru.pazik98.entity.soil;

import org.bukkit.Location;
import ru.pazik98.entity.plant.Plant;

public interface Soil {
    float getHumidity();

    float getFertilizer();

    float getTemperature();

    void increaseFertilizer(float count);

    void decreaseFertilizer(float count);

    void increaseWater(float count);

    void decreaseWater(float count);

    float getWater();

    float getWaterCapacity();

    Location getLocation();

    Plant getPlant();

    void setPlant(Plant plant);
}
