package org.arthur.salesman.evaluation;

import java.util.List;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.11
 */
public class Rmse {

    public static double evaluate(List<Double> original, List<Double> predicted) throws Exception {
        return Math.sqrt(Mae.evaluate(original, predicted));
    }

}
