package org.arthur.salesman.evaluation;

import org.arthur.salesman.model.Recommendation;

import java.util.List;

/**
 * This class calculates how much relevant items were found in the top k result for the recommender system
 *
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.12
 */
public class ScoreAtK {

    private ScoreAtK() { }

    public static double evaluate(List<Recommendation> original, List<Recommendation> predicted) throws Exception {
        if (original == null || original.size() == 0 || predicted == null || predicted.size() == 0) {
            throw new Exception("Problem with the given data");
        }

        for (Recommendation prediction : predicted) {
            if (original.contains(prediction)) {
                return 1.0;
            }
        }

        return .0;
    }

}
