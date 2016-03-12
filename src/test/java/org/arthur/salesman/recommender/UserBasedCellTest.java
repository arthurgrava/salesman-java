package org.arthur.salesman.recommender;

import junit.framework.Assert;
import org.arthur.salesman.BaseTest;
import org.arthur.salesman.model.Citation;
import org.arthur.salesman.model.Similar;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;

/**
 * Created by tutu on 2/4/16.
 */
public class UserBasedCellTest {
    private static Map<String, List<Citation>> citations;
    private static Map<String, List<Similar>> similars;
    private static Map<String, Double> means;

    @BeforeClass
    public static void settingUpThings() {
        citations = BaseTest.mutate(BaseTest.setUpCitations());
        similars = BaseTest.setUpSimilars();
        means = BaseTest.setUpMeans(citations);
    }

    @Test
    public void testCase() {
        String authorId = citations.keySet().iterator().next();
        UserBasedCell rec = new UserBasedCell(authorId, citations, similars.get(authorId), null, means, true);
        rec.run();

        Assert.assertTrue(1 == 1);
    }

}
