package ru.pazik98.event.handler;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import ru.pazik98.entity.container.EntityStateContainer;
import ru.pazik98.entity.plant.PlantState;
import ru.pazik98.entity.soil.SoilState;
import ru.pazik98.util.Convert;

import java.util.List;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class PlayerActionHandler {
    private final Logger logger = Bukkit.getLogger();
    private final EntityStateContainer container = EntityStateContainer.getInstance();

    public void makeFarmland(PlayerInteractEvent e) {
        if (isLand(e.getClickedBlock().getType())) container.createSoil(e.getClickedBlock());
    }

    public void harvestPlant(PlayerInteractEvent e) {
        if (!container.isPlant(e.getClickedBlock().getLocation())) return;
        Block block = e.getClickedBlock();
        List<ItemStack> harvest = container.getPlant(block.getLocation()).getCrops();

        harvest.stream()
                .map(x -> block.getLocation().getWorld().dropItem(block.getLocation(), x))
                .collect(Collectors.toSet())
                .forEach(x -> x.setPickupDelay(0));


        container.destroyPlant(block.getLocation());
        block.setType(Material.AIR);
        logger.warning("Harvested " + block.getType() + " at " + block.getLocation());

    }

    public void plantSeedling(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        logger.warning("Planting on " + block.getLocation());
        Location location = block.getLocation();
        container.createPlant(
                container.getSoil(location),
                new Location(location.getWorld(), location.getX(), location.getY() + 1, location.getBlockZ()),
                e.getMaterial()
        );
    }

    public void showPlantInfo(PlayerInteractEvent e) {
        PlantState plant = (PlantState) container.getPlant(e.getClickedBlock().getLocation());
        if (plant == null) return;
        StringBuilder message = new StringBuilder();
        message.append("_______________________\n");
        message.append(" --- ").append(plant.getPlantType()).append(" ---\n");
        message.append(" Happiness: ").append(Math.round(plant.getHappiness())).append("%\n");
        message.append(" Growth time: ").append(Convert.ticksToTime(plant.getUpdatesTickNumber())).append("\n");
        message.append(" Growth phase: ").append(plant.getGrowthPhase()).append("\n");
        message.append(" Maturity: ").append(plant.getMaturity() * 100).append("%\n");
        message.append(" Productivity: ").append(plant.getProductivity() * 100).append("%\n");
        message.append(" Decaying: ").append(plant.getDecay() * 100).append("%\n");
        message.append("_______________________\n");
        e.getPlayer().sendMessage(message.toString());
    }

    public void showSoilInfo(PlayerInteractEvent e) {
        SoilState soil = (SoilState) container.getSoil(e.getClickedBlock().getLocation());
        if (soil == null) return;
        StringBuilder message = new StringBuilder();
        message.append("_______________________\n");
        message.append(" --- SOIL ---\n");
        message.append(" Humidity: ").append(Convert.humidityToPercent(soil.getHumidity())).append("%\n");
        message.append(" Temperature: ").append(Convert.temperatureToDegrees(soil.getTemperature())).append("°с\n");
        message.append(" Water: ").append(soil.getWater()).append("/").append(soil.getWaterCapacity()).append("mB\n");
        message.append(" Fertilizer: ").append(soil.getFertilizer()).append("g\n");
        message.append("_______________________\n");
        e.getPlayer().sendMessage(message.toString());
    }

    private boolean isLand(Material m) {
        return m.equals(Material.GRASS_BLOCK) || m.equals(Material.DIRT_PATH) || m.equals(Material.DIRT);
    }
}
