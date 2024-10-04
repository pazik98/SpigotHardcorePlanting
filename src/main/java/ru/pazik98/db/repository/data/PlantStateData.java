package ru.pazik98.db.repository.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nivixx.ndatabase.api.annotation.Indexed;
import com.nivixx.ndatabase.api.annotation.NTable;
import com.nivixx.ndatabase.api.model.NEntity;
import lombok.*;
import ru.pazik98.entity.plant.PlantState;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@ToString
@NTable(name = "plant_state")
public class PlantStateData extends NEntity<Integer> {
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

    @EqualsAndHashCode.Include
    @JsonProperty("seed_material")
    private String seedMaterial;

    @EqualsAndHashCode.Include
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

    public PlantStateData(PlantState plantState) {
        this.x = plantState.getLocation().getBlockX();
        this.y = plantState.getLocation().getBlockY();
        this.z = plantState.getLocation().getBlockZ();
        this.worldUID = plantState.getLocation().getWorld().getUID();
        this.seedMaterial = plantState.getPlantType().getSeedMaterial().toString();
        this.plantingTick = plantState.getPlantingTick();
        this.updateTicks = plantState.getUpdatesTickNumber();
        this.growthPhase = plantState.getGrowthPhase();
        this.maturity = plantState.getMaturity();
        this.productivity = plantState.getProductivity();
        this.decay = plantState.getDecay();
        this.setKey(this.hashCode());
    }
}
