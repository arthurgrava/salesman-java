package org.arthur.salesman.evaluation;

import org.arthur.salesman.model.Recommendation;

import java.util.List;

/**
 * This class will calculate the Mean Absolute Error (MAE) of a dataset, if you need to check the formula, there is
 * plenty of papers talking about it.
 *
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.11
 */
public class Mae {

    private Mae() { }

    public static double evaluate(List<Recommendation> original, List<Recommendation> predicted) throws Exception {
        if (original.size() != predicted.size()) {
            throw new Exception("Cannot calculate RMSE on vectors with different size");
        }

        double sum = 0.0;
        for (int i = 0 ; i < original.size() ; i++) {
            sum += Math.pow((predicted.get(i).getScore() - original.get(i).getScore()), 2.0);
        }
        sum = (sum / Double.parseDouble(original.size() + ""));

        return sum;
    }

}
