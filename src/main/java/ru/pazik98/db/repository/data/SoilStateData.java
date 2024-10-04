package ru.pazik98.db.repository.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nivixx.ndatabase.api.annotation.Indexed;
import com.nivixx.ndatabase.api.annotation.NTable;
import com.nivixx.ndatabase.api.model.NEntity;
import lombok.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import ru.pazik98.entity.Plant;
import ru.pazik98.entity.PlantState;
import ru.pazik98.entity.SoilState;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@ToString
@NTable(name = "soil_state")
public class SoilStateData extends NEntity<Integer> {
    @EqualsAndHashCode.Include
    @JsonProperty("x")
    @Indexed
    private int x;

    @EqualsAndHashCode.Include
    @JsonProperty("y")
    private int y;

    @EqualsAndHashCode.Include
    @JsonProperty("z")
    @Indexed
    private int z;

    @EqualsAndHashCode.Include
    @JsonProperty("world_uid")
    @Indexed
    private UUID worldUID;

    @JsonProperty("humidity")
    private float humidity;

    @JsonProperty("temperature")
    private float temperature;

    @JsonProperty("fertilizer")
    private float fertilizer;

    @JsonProperty("water")
    private float water;

    @JsonProperty("plant")
    private PlantStateData plant;

    public SoilStateData(SoilState soilState) {
        this.x = soilState.getLocation().getBlockX();
        this.y = soilState.getLocation().getBlockY();
        this.z = soilState.getLocation().getBlockZ();
        this.worldUID = soilState.getLocation().getWorld().getUID();
        this.humidity = soilState.getHumidity();
        this.temperature = soilState.getTemperature();
        this.fertilizer = soilState.getFertilizer();
        this.water = soilState.getWater();
        if (soilState.getPlant() != null) this.plant = new PlantStateData((PlantState) soilState.getPlant());
        this.setKey(this.hashCode());
    }
}
