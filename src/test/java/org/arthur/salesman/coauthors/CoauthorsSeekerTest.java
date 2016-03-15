package org.arthur.salesman.coauthors;

import org.arthur.salesman.coauthors.CoauthorsSeeker;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

/**
 * Created by tutu on 2/12/16.
 */
public class CoauthorsSeekerTest {

    /**
     * It will use a local sample file to test if the solution is really working
     */
    @Test
    public void testIfItWorks() {
        String path = "/Users/tutu/personal/git/data/samples/publications.csv";
        int numberOfAuthors = 5;
        int numberOfArticles = 4;
        String separator = ",";

        CoauthorsSeeker cs = new CoauthorsSeeker(path, numberOfAuthors, numberOfArticles, separator);

        try {
            cs.calculate(true);
        } catch (Exception e) {
            Assert.fail();
        }

        Assert.assertTrue(cs.getCoauthorship().size() == numberOfAuthors);
        Assert.assertTrue(cs.getArticlesAuthors().size() == numberOfArticles);

        Map<String, Integer> coauthors = cs.getCoauthorship().get("grava ap");
        Assert.assertTrue(coauthors.containsKey("sardella sa"));
        Assert.assertTrue(coauthors.containsKey("digiampietri la"));
        Assert.assertTrue(coauthors.containsKey("forte n"));
    }

}
