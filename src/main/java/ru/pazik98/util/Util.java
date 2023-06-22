package ru.pazik98.util;

import java.util.Random;

public class Util {
    private static final Random random = new Random();

    public static int getRandomRound(float number) {
        int result = (int) number;
        if (getRandom(number - result)) return result + 1;
        else return result;
    }

    public static boolean getRandom(float chance) {
        return random.nextFloat() <= chance;
    }
}
