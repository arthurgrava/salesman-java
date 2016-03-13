package org.arthur.salesman.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.13
 */
public class Doubles {

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

}
