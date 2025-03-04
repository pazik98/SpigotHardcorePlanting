package ru.pazik98.entity.scanner.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Location;

@Getter
@AllArgsConstructor
public class EntityInfoError {
    private Location location;
    private EntityInfoErrorType errorType;
}
