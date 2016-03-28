package org.arthur.salesman.recommender;

import org.arthur.salesman.BaseTest;
import org.arthur.salesman.model.Citation;
import org.arthur.salesman.model.Similar;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Map;


/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.25
 */
public class TrustNetworkCellTest {
    private static Map<String, List<Citation>> citations;
    private static Map<String, List<Similar>> similars;

    @BeforeClass
    public static void settingUpThings() throws Exception {
        citations = BaseTest.mutate(BaseTest.setUpCitations());
        similars = BaseTest.setUpSimilars();
    }

    @Test
    public void testRun() throws Exception {
        for (String authorId : citations.keySet()) {
            TrustNetworkCell rec = new TrustNetworkCell(authorId, citations, similars.get(authorId), null, -1, 10);
            rec.run();
        }

        Assert.assertTrue(1 == 1);
    }
}