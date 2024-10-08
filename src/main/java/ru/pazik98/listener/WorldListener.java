package ru.pazik98.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import ru.pazik98.entity.container.EntityStateContainer;
import ru.pazik98.entity.plant.PlantType;
import ru.pazik98.plugin.HardcorePlanting;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class WorldListener implements Listener {

    private final Logger logger = Bukkit.getLogger();
    private final EntityStateContainer container = EntityStateContainer.getInstance();

    @EventHandler
    public void onPlantGrow(BlockGrowEvent e) {
        if (PlantType.isPlant(e.getBlock().getType())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        checkAndDestroy(e.getBlock().getLocation());
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        checkAndDestroy(e.getBlock().getLocation());
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent e) {
        checkAndDestroy(e.getBlock().getLocation());
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        EntityStateContainer.getInstance().load(e.getChunk());
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        EntityStateContainer.getInstance().unload(e.getChunk());
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent e) {
        handlePistonDestroy(e.getBlocks());
    }

    @EventHandler
    public void OnPistonRetract(BlockPistonRetractEvent e) {
        handlePistonDestroy(e.getBlocks());
    }

    private void checkAndDestroy(Location location) {
        if (container.isSoil(location)) container.destroySoil(location);
        if (container.isPlant(location)) container.destroyPlant(location);
    }

    private void handlePistonDestroy(List<Block> changedBlocks) {
        List<Block> blocks =  changedBlocks.stream()
                .filter(block -> container.isSoil(block.getLocation()) || container.isPlant(block.getLocation()))
                .toList();

        blocks.stream()
                .filter(block -> container.isSoil(block.getLocation()))
                .forEach(block -> block.getLocation().getBlock().setType(Material.DIRT));

        blocks.forEach(block -> checkAndDestroy(block.getLocation()));
    }
}
