package org.arthur.salesman.evaluation;

import org.arthur.salesman.BaseTest;
import org.arthur.salesman.model.Recommendation;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.13
 */
public class EvalCellTest {

    @Test
    public void testIfEvaluationsAreCalculated() {
        List<Recommendation> ratings = generateRatings(10);
        List<Recommendation> predictions = generateRatings(8);

        EvalCell ec = new EvalCell("arthur", ratings, predictions, null);
        ec.run();

        Assert.assertTrue(ec.mae > .0);
        Assert.assertTrue(ec.rmse > .0);
        Assert.assertTrue(ec.sAtK == 1.0);
    }

    private List<Recommendation> generateRatings(int size) {
        List<Recommendation> ratings = new ArrayList<>(10);
        for (int i = 0 ; i < size ; i++) {
            ratings.add(new Recommendation(i + "", Math.random()));
        }

        return ratings;
    }

}
