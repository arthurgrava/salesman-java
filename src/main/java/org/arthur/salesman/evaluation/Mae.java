package org.arthur.salesman.evaluation;

import java.util.List;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.11
 */
public class Mae {

    public static double evaluate(List<Double> original, List<Double> predicted) throws Exception {
        if (original.size() != predicted.size()) {
            throw new Exception("Cannot calculate RMSE on vectors with different size");
        }

        double sum = 0.0;
        for (int i = 0 ; i < original.size() ; i++) {
            sum += Math.pow((predicted.get(i) - original.get(i)), 2.0);
        }
        sum = (sum / Double.parseDouble(original.size() + ""));

        return sum;
    }

}
