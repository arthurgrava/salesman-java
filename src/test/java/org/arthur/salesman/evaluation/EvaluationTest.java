package org.arthur.salesman.evaluation;

import org.arthur.salesman.model.Recommendation;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.04.15
 */
public class EvaluationTest {

    Random random = new Random();

    @Test
    public void testIfItWorks() {
        List<Recommendation> ratings = generateRatings(16);
        List<Recommendation> predictions = generateRatings(10);

        Evaluation ec = new Evaluation(predictions, ratings, "authorId", null);
        ec.run();

        Assert.assertTrue(ec.predictions.get(0).getScore() > ec.predictions.get(1).getScore());
        Assert.assertEquals(1.0, ec.sAtK[4], .00000001);
    }

    private List<Recommendation> generateRatings(int size) {
        Set<Recommendation> ratings = new HashSet<>(size);
        for (int i = 0 ; i < size ; i++) {
            ratings.add(new Recommendation(random.nextInt(size / 2) + "", Math.random()));
        }
        return new ArrayList<>(ratings);
    }

}