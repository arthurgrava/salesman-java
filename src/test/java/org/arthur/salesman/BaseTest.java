package org.arthur.salesman;

import org.arthur.salesman.model.Citation;
import org.arthur.salesman.model.Recommendation;
import org.arthur.salesman.model.Similar;

import java.util.*;

/**
 * Created by tutu on 2/4/16.
 */
public class BaseTest {

    private static Random random = new Random();

    public static Map<String, List<Citation>> setUpCitations() {
        Map<String, List<Citation>> citations = new HashMap<>(10);
        for (int i = 0 ; i < 10 ; i++) {
            String authorId = "Author " + i;
            List<Citation> cits = new ArrayList<>(10);
            for (int j = 0 ; j < 10 ; j++) {
                Citation c = new Citation();
                c.setAuthorId(authorId);
                c.setArticleId("article " + (j + 1));
                c.setScore(random.nextDouble());
                cits.add(c);
            }
            citations.put(authorId, cits);
        }

        return citations;
    }

    public static Map<String, List<Similar>> setUpSimilars() {
        Map<String, List<Similar>> similars = new HashMap<>(10);
        for (int i = 0 ; i < 10 ; i++) {
            String authorId = "Author " + i;
            List<Similar> sims = new ArrayList<>(9);
            for (int j = 0 ; j < 10 ; j++) {
                if (i != j) {
                    Similar s = new Similar();
                    s.setAuthorId("Author " + j);
                    s.setScore(random.nextDouble());
                    sims.add(s);
                }
            }
            similars.put(authorId, sims);
        }
        return similars;
    }

    public static Map<String, Double> setUpMeans(Map<String, List<Citation>> citations) {
        Set<String> authors = citations.keySet();
        Map<String, Double> means = new HashMap<>(citations.size());
        for (String author : authors) {
            List<Citation> cits = citations.get(author);
            double mean = 0.0;
            for (Citation cit : cits) {
                mean += cit.getScore();
            }
            means.put(author, (mean / new Double(cits.size() + "")));
        }
        return means;
    }

    public static Map<String, List<Citation>> mutate(Map<String, List<Citation>> citations) {
        for (String id : citations.keySet()) {
            citations.get(id).remove(random.nextInt(citations.get(id).size()));
        }
        return citations;
    }

    public static Recommendation getRecommendation(double score) {
        return new Recommendation("item" + score, score);
    }

    public static List<Recommendation> getArray(Recommendation... elements) {
        List<Recommendation> arry = null;
        if (elements != null && elements.length > 0) {
            arry = new ArrayList<>(elements.length);
            Collections.addAll(arry, elements);
        }
        return arry;
    }

    public static List<Recommendation> getArray(String... elements) {
        List<Recommendation> arry = null;
        if (elements != null && elements.length > 0) {
            arry = new ArrayList<>(elements.length);
            for (String score : elements) {
                arry.add(getRecommendation(Double.parseDouble(score)));
            }
        }
        return arry;
    }

    public static List<Recommendation> getArray(int size) {
        List<Recommendation> arry = new ArrayList<>(size);
        for (double i = 0.0 ; i < size ; i++) {
            arry.add(new Recommendation("item" + i, i));
        }
        return arry;
    }

    public static List<String> getStringArray(int size) {
        List<String> arry = new ArrayList<>(size);
        for (int i = 0 ; i < size ; i++) {
            arry.add(i + "");
        }
        return arry;
    }

    public static List<String> getStringArray(String... elements) {
        List<String> arry = null;
        if (elements != null && elements.length > 0) {
            arry = new ArrayList<>(elements.length);
            for (String element : elements) {
                arry.add(element);
            }
        }
        return arry;
    }
}
