package org.arthur.salesman.evaluation;

import org.arthur.salesman.BaseTest;
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
            ScoreAtK.evaluate(new ArrayList<String>(), null);
            Assert.assertTrue(1 == 2);
        } catch (Exception e) {
            Assert.assertTrue(1 == 1);
        }
        try {
            ScoreAtK.evaluate(null, new ArrayList<String>());
            Assert.assertTrue(1 == 2);
        } catch (Exception e) {
            Assert.assertTrue(1 == 1);
        }
        try {
            ScoreAtK.evaluate(new ArrayList<String>(), new ArrayList<String>());
            Assert.assertTrue(1 == 2);
        } catch (Exception e) {
            Assert.assertTrue(1 == 1);
        }
    }

    @Test
    public void testShouldFindScoresAtK() throws Exception {
        List<String> orig = BaseTest.getStringArray(5);
        List<String> pred = BaseTest.getStringArray(4);

        double expected = .8;
        double score = ScoreAtK.evaluate(orig, pred);

        Assert.assertEquals(expected, score, .000000000001);
    }

    @Test
    public void testShouldFindScoresAtKNotContainingAll() throws Exception {
        List<String> orig = BaseTest.getStringArray("1", "x", "y", "z");
        List<String> pred = BaseTest.getStringArray("z", "k", "1");

        double expected = .5;
        double score = ScoreAtK.evaluate(orig, pred);

        Assert.assertEquals(expected, score, .000000000001);
    }

}
