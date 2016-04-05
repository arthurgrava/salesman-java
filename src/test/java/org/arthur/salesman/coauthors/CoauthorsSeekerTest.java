package org.arthur.salesman.coauthors;

import org.arthur.salesman.coauthors.CoauthorsSeeker;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
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
        String path = getResourcePath();
        int numberOfAuthors = 3882;
        int numberOfArticles = 799;
        String separator = ",";

        CoauthorsSeeker cs = new CoauthorsSeeker(path, numberOfAuthors, numberOfArticles, separator);

        try {
            cs.calculate(false);
        } catch (Exception e) {
            Assert.fail();
        }

        Assert.assertEquals(numberOfAuthors, cs.getCoauthorship().size());
        Assert.assertEquals(numberOfArticles, cs.getArticlesAuthors().size());

        Map<String, Integer> coauthors = cs.getCoauthorship().get("campos rr");
        Assert.assertTrue(coauthors.containsKey("tolentino-silva frp"));
        Assert.assertTrue(coauthors.containsKey("mello leam"));
    }

    private String getResourcePath() {
        File file = new File("src/main/resources/inline_publications.sample");
        return file.getAbsolutePath();
    }

}
