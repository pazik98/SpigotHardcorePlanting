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
        int hours = (int) ticks / (20 * 60 * 60);
        int minutes = (int) (ticks / (20 * 60)) - (hours * 60);
        int seconds = (int) (ticks / 20) - (minutes * 60) - (hours * 60 * 60);
        return hours + "h " + minutes + "m " + seconds + "s";
    }
}
