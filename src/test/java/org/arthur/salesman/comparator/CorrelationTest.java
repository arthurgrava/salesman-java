package org.arthur.salesman.comparator;

import org.arthur.salesman.BaseTest;
import org.arthur.salesman.model.Citation;
import org.arthur.salesman.model.Similarity;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * @author Arthur Grava (arthur at luizalabs.com).
 */
public class CorrelationTest {

    private static Map<String, List<Citation>> citations;

    @BeforeClass
    public static void settingUpThings() {
        citations = BaseTest.setUpCitations();
    }

    @Test
    public void shouldOrdenateSimilarUsersCorrectly() {
        Correlation corr = new Correlation(citations.keySet().iterator().next(), citations, 5, null, true);
        corr.run();

        Queue<Similarity> queue = corr.getTopK();
        Similarity max = Collections.max(queue);
        Assert.assertFalse(max.equals(queue.poll()));
    }

}
