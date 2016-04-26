package org.arthur.salesman.recommender;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arthur.salesman.model.Citation;
import org.arthur.salesman.model.Recommendation;
import org.arthur.salesman.model.Similar;
import org.arthur.salesman.utils.RecommenderUtils;

import java.io.BufferedWriter;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.04.26
 */
public class TSRSCell implements Runnable {

    private String author;
    private Map<String, List<Citation>> ratings;
    private List<Similar> neighbors;
    private Map<String, Double> means;
    private int topK;

    private BufferedWriter writer;
    private PriorityQueue<Recommendation> predictions;

    private static final Logger LOG = LogManager.getLogger(SRSCell.class);

    public TSRSCell(String author, Map<String, List<Citation>> ratings, List<Similar> neighbors,
                   Map<String, Double> means, int topK, BufferedWriter writer, int topNeighbors) {
        this.author = author;
        this.ratings = ratings;
        this.means = means;
        this.topK = topK;
        this.writer = writer;
        this.neighbors = RecommenderUtils.getTopNeighbors(neighbors, topNeighbors);
    }

    @Override
    public void run() {
        try {
            List<String> candidates = RecommenderUtils.fetchUnratedArticles(author, ratings, neighbors);

            if (candidates.isEmpty()) {
                LOG.debug("No unrated items found for author: " + author);
                return;
            }

            predictions = new PriorityQueue<>(this.topK);
            for (String candidateId : candidates) {
                double predictionScore = predict(candidateId);

                if (predictionScore != RecommenderUtils.WRONG) {
                    addPrediction(candidateId, predictionScore);
                }
            }

            RecommenderUtils.toFile(writer, predictions, author);
        } catch (Exception e) {
            LOG.error("Some error, please check", e);
        }
    }

    private void addPrediction(String itemId, double predictionScore) {
        if (predictions.size() >= topK) {
            if (predictions.peek().getScore() <= predictionScore) {
                return;
            }
            predictions.remove();
        }
        predictions.add(new Recommendation(itemId, predictionScore));
    }

    /**
     * Given an item it will calculate a prediction rating score for the {@link this.mainAuthor} on the specified item
     *
     * @param item item to get a predicted score
     * @return
     */
    private double predict(String item) {
        double num = 0.0;
        double denom = 0.0;

        for (Similar neighbor : neighbors) {
            String id = neighbor.getAuthorId();
            double rate = getRating(id, item);
            double mean = getMean(id);

            if (rate != RecommenderUtils.WRONG) {
                num += (neighbor.getScore() * (rate - mean));
            }
            denom += neighbor.getScore();
        }

        if (denom <= 0) {
            LOG.warn("The sum is 0, and it is weird: " + author + ", " + num + ", " + denom + ", " + item);
            return RecommenderUtils.WRONG;
        }

        return getMean(author) + (num / denom);
    }

    private Double getMean(String authorId) {
        return means.containsKey(authorId) ? means.get(authorId) : 0.0;
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

        return RecommenderUtils.WRONG;
    }

}
