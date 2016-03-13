package org.arthur.salesman.evaluation;

import org.arthur.salesman.BaseTest;
import org.arthur.salesman.model.Recommendation;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.12
 */
public class ScoreAtKTest {

    @Test
    public void testShouldThrowErrorOnIrregularParams() {
        try {
            ScoreAtK.evaluate(null, null);
            Assert.assertTrue(1 == 2);
        } catch (Exception e) {
            Assert.assertTrue(1 == 1);
        }
        try {
            ScoreAtK.evaluate(new ArrayList<Recommendation>(), null);
            Assert.assertTrue(1 == 2);
        } catch (Exception e) {
            Assert.assertTrue(1 == 1);
        }
        try {
            ScoreAtK.evaluate(null, new ArrayList<Recommendation>());
            Assert.assertTrue(1 == 2);
        } catch (Exception e) {
            Assert.assertTrue(1 == 1);
        }
        try {
            ScoreAtK.evaluate(new ArrayList<Recommendation>(), new ArrayList<Recommendation>());
            Assert.assertTrue(1 == 2);
        } catch (Exception e) {
            Assert.assertTrue(1 == 1);
        }
    }

    @Test
    public void testShouldFindScoresAtK() throws Exception {
        List<Recommendation> orig = BaseTest.getArray(5);
        List<Recommendation> pred = BaseTest.getArray(4);

        double expected = .8;
        double score = ScoreAtK.evaluate(orig, pred);

        Assert.assertEquals(expected, score, .000000000001);
    }

    @Test
    public void testShouldFindScoresAtKNotContainingAll() throws Exception {
        List<Recommendation> orig = BaseTest.getArray("1", "2", "3", "4");
        List<Recommendation> pred = BaseTest.getArray("1", "4", "5");

        double expected = .5;
        double score = ScoreAtK.evaluate(orig, pred);

        Assert.assertEquals(expected, score, .000000000001);
    }

}
