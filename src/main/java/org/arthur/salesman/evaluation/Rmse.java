package org.arthur.salesman.evaluation;

import org.arthur.salesman.model.Recommendation;

import java.util.List;

/**
 * This class will calculate the Root Mean Square Error (RMSE) of a predicted dataset, you can verify the formula on
 * a plenty number of papers.
 *
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.11
 */
public class Rmse {

    private Rmse() { }

    public static double evaluate(List<Recommendation> original, List<Recommendation> predicted) throws Exception {
        return Math.sqrt(Mae.evaluate(original, predicted));
    }
    public static double evaluate(Recommendation[] original, Recommendation[] predicted) throws Exception {
        return Math.sqrt(Mae.evaluate(original, predicted));
    }
    public static double evaluate(double[] original, double[] predicted) throws Exception {
        return Math.sqrt(Mae.evaluate(original, predicted));
    }

}
