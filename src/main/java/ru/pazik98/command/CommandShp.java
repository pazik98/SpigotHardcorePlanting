package ru.pazik98.command;

import org.bukkit.Chunk;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.pazik98.entity.scanner.ChunkScanner;
import ru.pazik98.entity.scanner.error.EntityInfoError;

import java.util.Set;

public class CommandShp implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("debug")) {
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                Chunk currentChunk = player.getLocation().getChunk();
                ChunkScanner chunkScanner = new ChunkScanner();
                Set<EntityInfoError> errors = chunkScanner.scanInfoErrors(currentChunk);
                player.sendMessage(String.format("Chunk %d %d scanned", currentChunk.getX(), currentChunk.getZ()));
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

        }
        return true;
    }
}
