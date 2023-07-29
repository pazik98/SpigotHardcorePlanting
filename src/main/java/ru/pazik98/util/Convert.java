package ru.pazik98.util;

public class Convert {

    public static float temperatureToDegrees(float temperature) {
        return temperature * 20.0f;
    }

    public static float degreesToTemperature(float degrees) {
        return degrees / 20.0f;
    }

    public static float humidityToPercent(float humidity) {
        return humidity * 100.0f;
    }

    public static String ticksToTime(long ticks) {
        int minutes = (int) ticks / 20 / 60;
        int hours = minutes / 60;
        if (hours > 0) minutes = minutes % hours;
        return hours + "h " + minutes + "m";
    }
}
