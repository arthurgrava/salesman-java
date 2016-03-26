package org.arthur.salesman.hybrid;

import org.arthur.salesman.BaseTest;
import org.arthur.salesman.model.Recommendation;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.26
 */
public class PickerMergeTest {

    @Test
    public void testDoMagic() throws Exception {
        Map<String, List<Recommendation>> recsA = BaseTest.getRecommendations(10);
        Map<String, List<Recommendation>> recsB = BaseTest.getRecommendations(20);

        Merge pm = new PickerMerge(recsA, recsB, null);
        pm.doMagic();

        Assert.assertTrue(1 == 1);
    }
}