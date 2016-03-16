package org.arthur.salesman.evaluation;

import org.arthur.salesman.model.Recommendation;

import java.util.List;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.15
 */
public class Mrr {

    private Mrr() { }

    public static double evaluate(List<Recommendation> original, List<Recommendation> predicted) throws Exception {
        if (original == null || original.size() == 0 || predicted == null || predicted.size() == 0) {
            throw new Exception("Problem with the given data");
        }

        for (int i = 0 ; i < predicted.size() ; i++) {
            Recommendation prediction = predicted.get(i);
            if (original.contains(prediction)) {
                return 1.0 / (i + 1);
            }
        }

        return 1.0 / 275000;
    }

}
