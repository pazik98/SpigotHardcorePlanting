package ru.pazik98.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nivixx.ndatabase.api.annotation.Indexed;
import com.nivixx.ndatabase.api.annotation.NTable;
import com.nivixx.ndatabase.api.model.NEntity;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@NTable(name = "plant_state")
public class PlantStateData extends NEntity<Integer> {
    @JsonProperty("x")
    @Indexed
    private int x;

    @JsonProperty("y")
    private int y;

    @JsonProperty("z")
    @Indexed
    private int z;

    @JsonProperty("world_uid")
    @Indexed
    private UUID worldUID;

    @JsonProperty("seed_material")
    private String seedMaterial;

    @JsonProperty("planting_tick")
    private long plantingTick;

    @JsonProperty("update_ticks")
    private long updateTicks;

    @JsonProperty("growth_phase")
    private int growthPhase;

    @JsonProperty("maturity")
    private float maturity;

    @JsonProperty("productivity")
    private float productivity;

    @JsonProperty("decay")
    private float decay;

//    PlantStateData(PlantState plantState) {
//        this.x = plantState.getLocation().getBlockX();
//        this.y = plantState.getLocation().getBlockY();
//        this.z = plantState.getLocation().getBlockZ();
//        this.worldId = plantState.getLocation().getWorld().getUID();
//        this.seedMaterial = plantState.getPlantType().getSeedMaterial().toString();
//        this.plantingTick = plantState.getPlantingTick();
//        this.updateTicks = plantState.getUpdatesTickNumber();
//        this.growthPhase = plantState.getGrowthPhase();
//        this.maturity = plantState.getMaturity();
//        this.productivity = plantState.getProductivity();
//        this.decay = plantState.getDecay();
//    }
}
