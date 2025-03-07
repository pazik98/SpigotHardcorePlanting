package ru.pazik98.command;

import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.pazik98.entity.scanner.ChunkFixer;
import ru.pazik98.entity.scanner.ChunkScanner;
import ru.pazik98.entity.scanner.error.EntityInfoError;

import java.util.Set;

public class CommandShp implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("debug")) {
            if (args.length > 1 && commandSender instanceof Player player) {
                switch (args[1].toLowerCase()) {
                    case "scan":
                        scanChunk(player);
                        break;
                    case "fix":
                        fixChunk(player);
                }
            }

        }
        return true;
    }

    private void scanChunk(Player player) {
        Chunk chunk = player.getLocation().getChunk();
        ChunkScanner chunkScanner = new ChunkScanner();
        Set<EntityInfoError> errors = chunkScanner.scanInfoErrors(chunk);
        player.sendMessage(String.format("Chunk %d %d scanned", chunk.getX(), chunk.getZ()));
        for (EntityInfoError error : errors) {
            player.sendMessage(String.format("Error %s at %d %d %d",
                    error.getErrorType().toString(),
                    error.getLocation().getBlockX(),
                    error.getLocation().getBlockY(),
                    error.getLocation().getBlockZ()));
        }
        if (errors.isEmpty()) {
            player.sendMessage("No errors found");
        }
    }

    private void fixChunk(Player player) {
        Chunk chunk = player.getLocation().getChunk();
        ChunkScanner chunkScanner = new ChunkScanner();
        Set<EntityInfoError> errors = chunkScanner.scanInfoErrors(chunk);
        ChunkFixer chunkFixer = new ChunkFixer();
        errors.forEach(chunkFixer::fix);
        player.sendMessage("Fixed " + errors.size() + " errors");
    }
}
