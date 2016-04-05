package org.arthur.salesman.coauthors;

import org.arthur.salesman.model.Coauthorship;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.04.04
 */
public class CoauthorsTimeContextSeekerTest {

    @Test
    public void testPopulate() throws Exception {
        String path = getResourcePath("inline_publications.sample");
        int nAuthors = 4645;

        CoauthorsTimeContextSeeker ctcs = new CoauthorsTimeContextSeeker(path, ",", nAuthors, 0, 1);
        ctcs.populate();

        Map<String, Set<Coauthorship>> pubs = ctcs.getPublications();

        Assert.assertNotNull(pubs);
        Assert.assertEquals(nAuthors, pubs.size());

        Set<Coauthorship> coauthors = pubs.get("campos rr");
        Coauthorship tolentino = new Coauthorship("tolentino-silva frp", 2003);
        Coauthorship mello = new Coauthorship("mello leam", 2003);

        Assert.assertTrue(coauthors.contains(mello));
        Assert.assertTrue(coauthors.contains(tolentino));
    }

    @Test
    public void testCalculate() throws IOException {
        String path = getResourcePath("publications_built.sample");
        int nAuthors = 4644;

        CoauthorsTimeContextSeeker ctcs = new CoauthorsTimeContextSeeker(path, ",", nAuthors, .25, 1);
        Map<String, Map<String, Double>> res = ctcs.calculate();

        Assert.assertNotNull(res);
    }

    private String getResourcePath(String filename) {
        File file = new File("src/main/resources/" + filename);
        return file.getAbsolutePath();
    }
}