package ru.pazik98.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.logging.Logger;

public class PlayerListener implements Listener {

    private Logger logger = Bukkit.getLogger();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Material material = e.getMaterial();
            Block block = e.getClickedBlock();

            // Check for making farmland
            if (isHoe(material) && isEarth(block.getType())) {
                logger.warning("creating entity!");
            }
        }
    }

    private boolean isHoe(Material m) {
        return m.equals(Material.WOODEN_HOE) || m.equals(Material.STONE_HOE) || m.equals(Material.IRON_HOE) ||
                m.equals(Material.GOLDEN_HOE) || m.equals(Material.DIAMOND_HOE) || m.equals(Material.NETHERITE_HOE);
    }

    private boolean isEarth(Material m) {
        return m.equals(Material.GRASS_BLOCK) || m.equals(Material.DIRT_PATH) || m.equals(Material.DIRT);
    }
}
