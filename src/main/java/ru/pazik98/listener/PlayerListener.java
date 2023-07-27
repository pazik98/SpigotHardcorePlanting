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
            if (material.equals(Material.PAPER) && PlantType.isPlant(block.getType())) {
                PlantState plant = plantManager.getPlant(block.getLocation());
                if (plant == null) return;
                StringBuilder message = new StringBuilder();
                message.append(" --- ").append(plant.getPlantType()).append(" ---\n");
                message.append(" Soil humidity: ").append(plant.getSoil().getHumidity()).append("\n");
                message.append(" Soil temperature: ").append(plant.getSoil().getTemperature()).append("\n");
                message.append(" Water: ").append(plant.getSoil().getWater()).append("/100\n");
                message.append(" Fertilizer: ").append(plant.getSoil().getFertilizer()).append("\n");
                message.append(" --- ");
                e.getPlayer().sendMessage(message.toString());
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
