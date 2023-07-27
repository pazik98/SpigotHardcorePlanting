package ru.pazik98.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import ru.pazik98.entity.PlantManager;
import ru.pazik98.entity.PlantType;

import java.util.logging.Logger;

public class WorldListener implements Listener {

    private Logger logger = Bukkit.getLogger();
    private PlantManager plantManager = PlantManager.getInstance();

    @EventHandler
    public void onPlantGrow(BlockGrowEvent e) {
        if (PlantType.isPlant(e.getBlock().getType())) {
            e.setCancelled(true);
            return;
        }
        logger.warning("Grow event at [" + e.getNewState().getLocation() + "]");
        logger.warning(e.getNewState().getBlock().getBlockData().getAsString());
        logger.warning("Time: " + Bukkit.getWorlds().get(0).getTime() + "\n");
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getBlock().getType().equals(Material.FARMLAND)) {
            plantManager.removeSoil(e.getBlock().getLocation());
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        if (e.getBlock().getType().equals(Material.FARMLAND)) {
            plantManager.removeSoil(e.getBlock().getLocation());
        }
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent e) {
        if (e.getBlock().getBlockData().getMaterial().equals(Material.FARMLAND)) {
            plantManager.removeSoil(e.getBlock().getLocation());
        }
    }
}
