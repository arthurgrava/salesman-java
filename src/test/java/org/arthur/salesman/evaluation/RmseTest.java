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
public class RmseTest {

    @Test(expected = Exception.class)
    public void testShouldThrowErrorWhenArraysDoNotHaveSameLength() throws Exception {
        List<Recommendation> orig = BaseTest.getArray(3);
        List<Recommendation> pred = BaseTest.getArray(2);

        Rmse.evaluate(orig, pred);
        Assert.assertTrue(1 == 2);
    }

    @Test
    public void testShouldCalculateRmseCorrectly() throws Exception {
        List<Recommendation> orig = BaseTest.getArray("1.0", "1.3", "1.5", "1.98", "1.2", "0.0");
        List<Recommendation> pred = BaseTest.getArray("1.2", "1.9", "1.46", "1.95", "1.9", "1.99");

        double expected = .899314553795;
        double rmse = Rmse.evaluate(orig, pred);

        Assert.assertEquals(expected, rmse, .0000000001);
    }

    @Test
    public void testShouldGetZeroWhenZerosArray() throws Exception {
        List<Recommendation> orig = BaseTest.getArray("0.0", "0.0");
        List<Recommendation> pred = BaseTest.getArray("0.0", "0.0");

        double expected = 0;
        double rmse = Rmse.evaluate(orig, pred);

        Assert.assertEquals(expected, rmse, .0000000001);
    }

    @Test
    public void testShouldNotGetDivisionByZeroError() throws Exception {
        Rmse.evaluate(new ArrayList<Recommendation>(), new ArrayList<Recommendation>());
        Assert.assertTrue(1 == 1);
    }

}
