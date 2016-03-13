package org.arthur.salesman.evaluation;

import org.arthur.salesman.BaseTest;
import org.arthur.salesman.model.Recommendation;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.11
 */
public class MaeTest {

    @Test(expected = Exception.class)
    public void testShouldThrowErrorWhenArraysDoNotHaveSameLength() throws Exception {
        List<Recommendation> orig = BaseTest.getArray(3);
        List<Recommendation> pred = BaseTest.getArray(2);

        Mae.evaluate(orig, pred);
        Assert.assertTrue(1 == 2);
    }

    @Test
    public void testShouldCalculateMaeCorrectly() throws Exception {
        List<Recommendation> orig = BaseTest.getArray("1.0", "1.3", "1.5", "1.98", "1.2", "0.0");
        List<Recommendation> pred = BaseTest.getArray("1.2", "1.9", "1.46", "1.95", "1.9", "1.99");

        double expected = .808766666666;
        double rmse = Mae.evaluate(orig, pred);

        Assert.assertEquals(expected, rmse, .0000000001);
    }

    @Test
    public void testShouldGetZeroWhenZerosArray() throws Exception {
        List<Recommendation> orig = BaseTest.getArray(BaseTest.getRecommendation(.0), BaseTest.getRecommendation(.0));
        List<Recommendation> pred = BaseTest.getArray(BaseTest.getRecommendation(.0), BaseTest.getRecommendation(.0));

        double expected = 0;
        double rmse = Mae.evaluate(orig, pred);

        Assert.assertEquals(expected, rmse, .0000000001);
    }

    @Test
    public void testShouldNotGetDivisionByZeroError() throws Exception {
        Mae.evaluate(new ArrayList<Recommendation>(), new ArrayList<Recommendation>());
        Assert.assertTrue(1 == 1);
    }

}
