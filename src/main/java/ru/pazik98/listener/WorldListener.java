package ru.pazik98.listener;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockGrowEvent;

import java.util.logging.Logger;

public class WorldListener implements Listener {

    private Logger logger = Bukkit.getLogger();

    @EventHandler
    public void onPlantGrow(BlockGrowEvent e) {
        logger.warning("Grow event at [" + e.getNewState().getLocation() + "]");
        logger.warning(e.getNewState().getBlock().getBlockData().getAsString());
        logger.warning("Time: " + Bukkit.getWorlds().get(0).getTime() + "\n");
    }
}
