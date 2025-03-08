package ru.pazik98.listener;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import ru.pazik98.entity.container.EntityStateContainer;
import ru.pazik98.entity.plant.PlantPresetManager;
import ru.pazik98.entity.scanner.ChunkFixer;
import ru.pazik98.entity.scanner.ChunkScanner;
import ru.pazik98.entity.soil.Soil;
import ru.pazik98.plugin.HardcorePlanting;

import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class WorldListener implements Listener {

    private final Logger logger = Bukkit.getLogger();
    private final EntityStateContainer container = EntityStateContainer.getInstance();

    @EventHandler
    public void onPlantGrow(BlockGrowEvent e) {
        if (PlantPresetManager.isPlant(e.getBlock().getType())) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        checkAndDestroyState(e.getBlock().getLocation());
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        checkAndDestroyState(e.getBlock().getLocation());
    }

    @EventHandler
    public void OnEntityExplode(EntityExplodeEvent e) {
        for (Block block : e.blockList())
        {
            checkAndDestroyState(block.getLocation());
        }
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent e) {
        checkAndDestroyState(e.getBlock().getLocation());
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        if (e.isNewChunk()) {
            var chunkScanner = new ChunkScanner();
            var chunkFixer = new ChunkFixer();
            chunkScanner.scanInfoErrors(e.getChunk()).forEach(chunkFixer::fix);
        }

        EntityStateContainer.getInstance().load(e.getChunk());
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        EntityStateContainer.getInstance().unload(e.getChunk());
    }

    @EventHandler()
    public void onPistonExtend(BlockPistonExtendEvent e) {
        handlePistonDestroy(e.getBlocks());
    }

    @EventHandler()
    public void onPistonRetract(BlockPistonRetractEvent e) {
        handlePistonDestroy(e.getBlocks());
    }

    @EventHandler
    public void onDropItem(BlockDropItemEvent e) {
    }

    @EventHandler
    public void onEntityChangeEvent(EntityChangeBlockEvent e) {

    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent e) {
        if (e.getEntity().getType() == EntityType.VILLAGER && (PlantPresetManager.isPlant(e.getBlock().getType()) || PlantPresetManager.isPlant(e.getTo()) )) {
            e.setCancelled(true);
        }
    }

    private void checkAndDestroyState(Location location) {
        if (container.isSoil(location)) container.destroySoil(location);
        if (container.isPlant(location)) container.destroyPlant(location);
    }

    private void handlePistonDestroy(List<Block> changedBlocks) {
        List<Block> blocks = changedBlocks.stream()
                .filter(block -> container.isSoil(block.getLocation()) || container.isPlant(block.getLocation()))
                .toList();

        List<Block> soilBlocks = blocks.stream().filter(block -> container.isSoil(block.getLocation())).toList();
        soilBlocks.forEach(block -> block.setType(Material.DIRT));
        soilBlocks.stream()
                .map(block -> container.getSoil(block.getLocation()))
                .map(Soil::getPlant)
                .filter(Objects::nonNull)
                .forEach(plant -> plant.getLocation().getBlock().setType(Material.AIR));

        blocks.stream()
                .filter(block -> container.isPlant(block.getLocation()))
                .forEach(block -> block.setType(Material.AIR));

        blocks.forEach(block -> checkAndDestroyState(block.getLocation()));
    }
}
