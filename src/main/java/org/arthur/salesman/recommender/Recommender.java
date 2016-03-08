package org.arthur.salesman.recommender;

import org.arthur.salesman.model.Citation;
import org.arthur.salesman.model.Similar;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Arthur Grava (arthur at luizalabs.com).
 */
public class Recommender implements Runnable {

    private String mainAuthor;
    private Map<String, List<Citation>> ratings;
    private List<Similar> similars;
    private Map<String, Double> means;
    private BufferedWriter writer;
    private boolean debug;

    private static final double WRONG = -999;

    public Recommender(String mainAuthor, Map<String, List<Citation>> ratings, List<Similar> similars,
                       BufferedWriter bw, Map<String, Double> means, boolean debug) {
        this.mainAuthor = mainAuthor;
        this.ratings = ratings;
        this.similars = similars;
        this.writer = bw;
        this.means = means;
        this.debug = debug;
    }

    @Override
    public void run() {
        try {
            calculate();
        } catch (IOException e) {
            if (debug) {
                System.err.println(e);
            }
        }
    }

    /**
     * For an user, it will calculate all predictions for his possible items
     *
     * @throws IOException
     */
    private void calculate() throws IOException {
        List<String> articles = getUnRatedArticles();

        if (articles.isEmpty()) {
            if (debug) {
                System.err.println("Nothing done because there is no items to evaluate");
            }
            return;
        }

        List<String> predictions = new ArrayList<>(articles.size());

        for (String item : articles) {
            double prediction = calculateItemPrediction(item);

            if (prediction != WRONG) {
                predictions.add(this.mainAuthor + "," + item + "," + prediction);
            }
        }

        writeOnFile(predictions);
    }

    private void writeOnFile(List<String> predictions) throws IOException {
        if (this.writer != null) {
            for (String line : predictions) {
                if (debug) {
                    System.out.println(line);
                }
                this.writer.write(line);
                this.writer.newLine();
            }
            this.writer.flush();
        } else {
            for (String line : predictions) {
                System.out.println(line);
            }
        }
    }

    /**
     * Given an item it will calculate a prediction rating score for the {@link this.mainAuthor} on the specified item
     *
     * @param item item to get a predicted score
     * @return
     */
    private double calculateItemPrediction(String item) {
        double up = 0.0;
        double down = 0.0;

        for (Similar similar : similars) {
            double ratingItem = getRating(similar.getAuthorId(), item);
            double similarMean = getMean(similar);

            if (ratingItem != WRONG) {
                up += (similar.getScore() * (ratingItem - similarMean));
                down += similar.getScore();
            }
        }

        if (down == 0) {
            if (debug) {
                System.err.println("The sum of similarities is zero and it is weird");
            }
            return WRONG;
        }

        return this.means.get(this.mainAuthor) + (up / down);
    }

    private Double getMean(Similar similar) {
        String authorId = similar.getAuthorId();
        return this.means.containsKey(authorId) ? this.means.get(authorId) : WRONG;
    }

    /**
     * Gets the rating of a similar user to {@link this.mainAuthor} on the specified item
     *
     * @param authorId  Similar author
     * @param item      Item to be rated
     * @return
     */
    private double getRating(String authorId, String item) {
        List<Citation> temp = this.ratings.get(authorId);

        if (temp != null) {
            for (Citation citation : temp) {
                if (item.equals(citation.getArticleId())) {
                    return citation.getScore();
                }
            }
        }

        return WRONG;
    }

    /**
     * Gets the list of items that the {@link this.mainAuthor} may be interested in because he didn't rate it yet
     *
     * @return
     */
    private List<String> getUnRatedArticles() {
        List<Citation> rated = this.ratings.get(this.mainAuthor);
        List <String> unrated = new ArrayList<>();

        for (Similar similar : this.similars) {
            String authorId = similar.getAuthorId();
            if (!this.ratings.containsKey(authorId)) {
                continue;
            }
            if (rated != null) {
                for (Citation citation : this.ratings.get(authorId)) {
                    if (!rated.contains(citation) && !unrated.contains(citation.getArticleId())) {
                        unrated.add(citation.getArticleId());
                    }
                }
            }
        }

        return unrated;
    }

    public static void main(String... args) {

    }
}
