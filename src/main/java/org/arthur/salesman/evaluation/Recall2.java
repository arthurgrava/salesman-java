package org.arthur.salesman.evaluation;

import org.arthur.salesman.model.Recommendation;

import java.util.List;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.04.17
 */
public class Recall2 {

    public static double evaluate(List<Recommendation> original, List<Recommendation> predicted) throws Exception {
        if (original == null || original.size() == 0 || predicted == null || predicted.size() == 0) {
            throw new Exception("Problem with the given data");
        }

        double hits = .0;
        for (Recommendation recommendation : original) {
            if (predicted.contains(recommendation)) {
                hits += 1.0;
            }
        }

        return hits / (double) original.size();
    }

}
