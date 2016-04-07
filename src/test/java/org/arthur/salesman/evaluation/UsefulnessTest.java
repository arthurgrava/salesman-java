package org.arthur.salesman.evaluation;

import org.arthur.salesman.BaseTest;
import org.arthur.salesman.model.Recommendation;
import org.arthur.salesman.model.Used;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.04.07
 */
public class UsefulnessTest {

    public static List<Recommendation> original;
    public static List<Recommendation> predicted;

    @BeforeClass
    public static void settingUp() {
        original = BaseTest.getArray(5);
        predicted = BaseTest.getArray(15);
    }

    @Test
    public void shouldMarkFiveUsedRecommendations() throws Exception {
        List<Used> useful = Usefulness.evaluate(original, predicted);

        Assert.assertNotNull(useful);

        int count = 0;
        for (Used used : useful) {
            if (used.getUseful() == 1) {
                count++;
            }
        }

        Assert.assertEquals(5, count);
    }

}