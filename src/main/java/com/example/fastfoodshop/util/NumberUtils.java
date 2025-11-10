package com.example.fastfoodshop.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ThreadLocalRandom;

public class NumberUtils {
    public static int roundToThousand(int value) {
        return (value / 1000) * 1000;
    }

    public static double roundToOneDecimal(double value) {
        BigDecimal bigDecimald = BigDecimal.valueOf(value);
        bigDecimald = bigDecimald.setScale(1, RoundingMode.HALF_UP);
        return bigDecimald.doubleValue();
    }

    public static int randomNumber(int min, int max) {
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
