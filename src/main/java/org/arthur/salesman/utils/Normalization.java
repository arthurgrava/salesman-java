package org.arthur.salesman.utils;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.04.04
 */
public class Normalization {

    public static double feature(double min, double max, double value, double ini, double end) {
        return max == min ? 1.0 : ini + ( ( (value - min) * (end - ini)) / (max - min) );
    }

    public static double feature(double min, double max, double value) {
        return feature(min, max, value, 0, 1);
    }

}
