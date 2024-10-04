package ru.pazik98.listener;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockGrowEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import ru.pazik98.entity.container.EntityStateContainer;
import ru.pazik98.entity.plant.PlantType;

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
        if (e.getBlock().getType().equals(Material.FARMLAND)) {
            container.destroySoil(e.getBlock().getLocation());
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent e) {
        if (e.getBlock().getType().equals(Material.FARMLAND)) {
            container.destroySoil(e.getBlock().getLocation());
        }
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent e) {
        if (e.getBlock().getBlockData().getMaterial().equals(Material.FARMLAND)) {
            container.destroySoil(e.getBlock().getLocation());
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent e) {
        EntityStateContainer.getInstance().load(e.getChunk());
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        EntityStateContainer.getInstance().unload(e.getChunk());
    }
}
