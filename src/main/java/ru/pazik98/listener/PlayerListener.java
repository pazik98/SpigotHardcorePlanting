package ru.pazik98.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import ru.pazik98.entity.PlantManager;
import ru.pazik98.entity.PlantState;
import ru.pazik98.entity.PlantType;
import ru.pazik98.entity.SoilState;
import ru.pazik98.util.Convert;

import java.util.List;
import java.util.logging.Logger;

public class PlayerListener implements Listener {

    private Logger logger = Bukkit.getLogger();
    private PlantManager plantManager = PlantManager.getInstance();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Material material = e.getMaterial();
            Block block = e.getClickedBlock();

            if (isHoe(material)) {
                // Check for making farmland
                if (isSoil(block.getType())) plantManager.createSoil(e.getClickedBlock());

                // Check for harvesting
                if (isPlant(block.getLocation())) {
                    List<ItemStack> harvest = plantManager.getPlant(block.getLocation()).getCrops();
                    for (ItemStack itemStack : harvest) {
                        Item item = block.getLocation().getWorld().dropItem(block.getLocation(), itemStack);
                        item.setPickupDelay(0);
                    }
                    plantManager.removePlant(block.getLocation());
                    block.setType(Material.AIR);
                    logger.warning("Harvested " + block.getType() + " at " + block.getLocation());
                }
            }

            // Check for planting
            if (PlantType.getPlantType(material) != null && block.getType().equals(Material.FARMLAND)) {
                logger.warning("Planting on " + block.getLocation());
                plantManager.createPlant(material, block);
            }

            // Check for research
            if (material.equals(Material.PAPER)) {
                // Click to plant
                if (PlantType.isPlant(block.getType())) {
                    PlantState plant = plantManager.getPlant(block.getLocation());
                    if (plant == null) return;
                    StringBuilder message = new StringBuilder();
                    message.append(" --- ").append(plant.getPlantType()).append(" ---\n");
                    message.append(" Happiness: ").append(Math.round(plant.getHappiness())).append("%\n");
                    message.append(" Growth time: ").append(Convert.ticksToTime(plant.getUpdatesTickNumber())).append("\n");
                    message.append(" Growth phase: ").append(plant.getGrowthPhase()).append("\n");
                    message.append(" Maturity: ").append(plant.getMaturity() * 100).append("%\n");
                    message.append(" Productivity: ").append(plant.getProductivity() * 100).append("%\n");
                    message.append(" Decaying: ").append(plant.getDecay() * 100).append("%\n");
                    message.append(" ------- ");
                    e.getPlayer().sendMessage(message.toString());
                }
                // Click to farmland
                if (block.getType().equals(Material.FARMLAND)) {
                    SoilState soil = plantManager.getSoil(block.getLocation());
                    if (soil == null) return;
                    StringBuilder message = new StringBuilder();
                    message.append(" --- SOIL ---\n");
                    message.append(" Humidity: ").append(Convert.humidityToPercent(soil.getHumidity())).append("%\n");
                    message.append(" Temperature: ").append(Convert.temperatureToDegrees(soil.getTemperature())).append("°с\n");
                    message.append(" Water: ").append(soil.getWater()).append("/").append(soil.getWaterCapacity()).append("mB\n");
                    message.append(" Fertilizer: ").append(soil.getFertilizer()).append("g\n");
                    message.append(" ------- ");
                    e.getPlayer().sendMessage(message.toString());
                }
            }
        }
    }

    private boolean isHoe(Material m) {
        return m.equals(Material.WOODEN_HOE) || m.equals(Material.STONE_HOE) || m.equals(Material.IRON_HOE) ||
                m.equals(Material.GOLDEN_HOE) || m.equals(Material.DIAMOND_HOE) || m.equals(Material.NETHERITE_HOE);
    }

    private boolean isSoil(Material m) {
        return m.equals(Material.GRASS_BLOCK) || m.equals(Material.DIRT_PATH) || m.equals(Material.DIRT);
    }

    private boolean isPlant(Location location) {
        return plantManager.getPlant(location) != null;
    }
}
