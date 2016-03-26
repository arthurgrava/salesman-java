package org.arthur.salesman.evaluation;

import org.arthur.salesman.model.Recommendation;

import java.util.List;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.26
 */
public class Precision {

    public static double evaluate(List<Recommendation> original, List<Recommendation> predicted, int k) throws Exception {
        if (original == null || original.size() == 0 || predicted == null || predicted.size() == 0) {
            throw new Exception("Problem with the given data");
        }

        return Recall.evaluate(original, predicted) / (k * 1.0);
    }

    public static double evaluate(double recall, int k) throws Exception {
        return recall / (k * 1.0);
    }

}
