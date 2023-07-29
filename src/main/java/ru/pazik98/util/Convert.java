package ru.pazik98.util;

public class Convert {

    public static float temperatureToDegrees(float temperature) {
        return temperature * 20.0f;
    }

    public static float humidityToPercent(float humidity) {
        return humidity * 100.0f;
    }
}
