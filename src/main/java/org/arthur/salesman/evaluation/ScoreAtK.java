package org.arthur.salesman.evaluation;

import java.util.List;

/**
 * This class calculates how much relevant items were found in the top k result for the recommender system
 *
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.12
 */
public class ScoreAtK {

    private ScoreAtK() { }

    public static double evaluate(List<String> original, List<String> predicted) throws Exception {
        if (original == null || original.size() == 0 || predicted == null || predicted.size() == 0) {
            throw new Exception("Problem with the given data");
        }

        double scores = .0;
        for (String prediction : predicted) {
            if (original.contains(prediction)) {
                scores++;
            }
        }

        return scores / original.size();
    }

}
