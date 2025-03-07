package ru.pazik98.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import ru.pazik98.entity.container.EntityStateContainer;
import ru.pazik98.entity.plant.PlantPresetManager;
import ru.pazik98.event.handler.PlayerActionHandler;

import java.util.logging.Logger;

public class PlayerListener implements Listener {

    private final Logger logger = Bukkit.getLogger();
    private final EntityStateContainer container = EntityStateContainer.getInstance();
    private final PlayerActionHandler handler = new PlayerActionHandler();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Material material = e.getMaterial();
            Block block = e.getClickedBlock();

            if (isHoe(material)) {
                handler.makeFarmland(e);

                // TODO: replace isSeedling by isPlant
                if (isSeedling(block.getLocation())) {
                    handler.harvestPlant(e);
                }
            }

            if (PlantPresetManager.getPresetBySeed(material) != null && block.getType().equals(Material.FARMLAND)) {
                handler.plantSeedling(e);
            }

            if (material.equals(Material.PAPER)) {
                if (PlantPresetManager.isPlant(block.getType())) {
                    handler.showPlantInfo(e);
                }

                if (block.getType().equals(Material.FARMLAND)) {
                    handler.showSoilInfo(e);
                }
            }
        }
    }

    private boolean isHoe(Material m) {
        return m.equals(Material.WOODEN_HOE) || m.equals(Material.STONE_HOE) || m.equals(Material.IRON_HOE) ||
                m.equals(Material.GOLDEN_HOE) || m.equals(Material.DIAMOND_HOE) || m.equals(Material.NETHERITE_HOE);
    }

    private boolean isLand(Material m) {
        return m.equals(Material.GRASS_BLOCK) || m.equals(Material.DIRT_PATH) || m.equals(Material.DIRT);
    }

    private boolean isSeedling(Location location) {
        return container.getPlant(location) != null;
    }
}
