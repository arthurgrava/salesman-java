package org.arthur.salesman.evaluation;

import org.arthur.salesman.BaseTest;
import org.arthur.salesman.model.Recommendation;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.04.17
 */
public class Recall2Test {

    @Test
    public void testIfItIsWorkingFine() throws Exception {
        List<Recommendation> original = BaseTest.getArray(10);
        List<Recommendation> prediction = BaseTest.getArray(3);

        double recall = Recall2.evaluate(original, prediction);

        double matches = (double) prediction.size();
        double expected = matches / (double) original.size();

        Assert.assertEquals(expected, recall, .00000001);
    }

}