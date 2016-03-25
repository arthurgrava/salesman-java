package org.arthur.salesman.recommender;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.arthur.salesman.model.Citation;
import org.arthur.salesman.model.Recommendation;
import org.arthur.salesman.model.Similar;
import org.arthur.salesman.model.Similarity;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * @author Arthur Grava (arthur at luizalabs.com).
 */
public class UserBasedCell implements Runnable {

    private String mainAuthor;
    private Map<String, List<Citation>> ratings;
    private List<Similar> similars;
    private Map<String, Double> means;
    private BufferedWriter writer;
    private int topK;
    private PriorityQueue<Recommendation> predictions;

    private static final double WRONG = -999;
    private static final Logger LOG = LogManager.getLogger(UserBasedCell.class);
    private static final int DEFAULT_TOPK = 50;

    public UserBasedCell(String mainAuthor, Map<String, List<Citation>> ratings, List<Similar> similars,
                         BufferedWriter bw, Map<String, Double> means, int topK) {
        this.mainAuthor = mainAuthor;
        this.ratings = ratings;
        this.similars = similars;
        this.writer = bw;
        this.means = means;
        this.topK = topK == -1 ? DEFAULT_TOPK : topK;
    }

    @Override
    public void run() {
        try {
            List<String> unrated = fetchUnratedArticles();

            if (unrated.isEmpty()) {
                LOG.info("No unrated items found for author: " + this.mainAuthor);
                return;
            }

            predictions = new PriorityQueue<>(this.topK);
            for (String item : unrated) {
                double prediction = predict(item);

                if (prediction != WRONG) {
                    addPrediction(item, prediction);
                }
            }

            writeOnFile(predictions);
        } catch (Exception e) {
            LOG.error("Some error, please check", e);
        }
    }

    private void addPrediction(String itemId, double prediction) {
        if (this.predictions.size() >= this.topK ) {
            Recommendation lower = this.predictions.peek();
            if (lower.getScore() >= prediction) {
                return;
            }
            this.predictions.remove();
        }

        this.predictions.add(new Recommendation(itemId, prediction));
    }

    /**
     * Gets the list of items that the {@link this.mainAuthor} may be interested in because he didn't rate it yet
     *
     * @return
     */
    private List<String> fetchUnratedArticles() {
        List<Citation> ratedItems = this.ratings.get(this.mainAuthor);

        if (ratedItems == null) {
            ratedItems = Collections.emptyList();
            LOG.info("Author has no citations: " + this.mainAuthor);
        } else if (this.similars == null) {
            LOG.error("No similar authors to this author, it is weird: " + this.mainAuthor);
            return Collections.emptyList();
        }

        Set<String> unratedItems = new HashSet<>();

        LOG.info("fetch unrated items: " + this.mainAuthor + ", " + ratedItems.size() + ", " + this.similars.size());

        for (Similar similar : this.similars) {
            String similarId = similar.getAuthorId();
            if (this.ratings.containsKey(similarId)) {
                List<Citation> similarRatings = this.ratings.get(similarId);
                LOG.info("Data: " + this.mainAuthor + "=" + ratedItems.size() + ", " + similarId + "=" + similarRatings.size());
                for (Citation citation : similarRatings) {
                    if (!ratedItems.contains(citation)) {
                        unratedItems.add(citation.getArticleId());
                    }
                }
            }
        }

        return new ArrayList<>(unratedItems);
    }

    /**
     * Given an item it will calculate a prediction rating score for the {@link this.mainAuthor} on the specified item
     *
     * @param item item to get a predicted score
     * @return
     */
    private double predict(String item) {
        double up = 0.0;
        double down = 0.0;

        for (Similar similar : similars) {
            String similarId = similar.getAuthorId();
            double ratedItem = getRating(similarId, item);
            double similarMean = getMean(similarId);

            if (ratedItem != WRONG) {
                up += (similar.getScore() * (ratedItem - similarMean));
            }
            down += similar.getScore();
        }

        if (down == 0) {
            LOG.warn("The sum of similarities is 0, and it is weird: " + this.mainAuthor + ", " + up + ", " + down + ", " + item);
            return WRONG;
        }

        return getMean(this.mainAuthor) + (up / down);
    }

    private void writeOnFile(Queue<Recommendation> predictions) throws IOException {
        if (this.writer != null) {
            for (Recommendation rec : predictions) {
                String line = this.mainAuthor + "," + rec.getItemId() + "," + rec.getScore();
                this.writer.write(line);
                this.writer.newLine();
                LOG.debug("prediction: " + line);
            }
            this.writer.flush();
        } else {
            for (Recommendation rec : predictions) {
                System.out.println(this.mainAuthor + "," + rec.getItemId() + "," + rec.getScore());
            }
        }
    }

    private Double getMean(String authorId) {
        return this.means.containsKey(authorId) ? this.means.get(authorId) : 0.0;
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
}
