package org.arthur.salesman.recommender;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arthur.salesman.model.Citation;
import org.arthur.salesman.model.Recommendation;
import org.arthur.salesman.model.Similar;

import java.io.BufferedWriter;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.25
 */
public class TrustNetworkCell implements Runnable {

    private static final Logger LOG = LogManager.getLogger(TrustNetworkCell.class);
    private static final int DEFAULT_TOPK = 50;

    private String authorId;
    private Map<String, List<Citation>> ratings;
    private List<Similar> trusted;
    private BufferedWriter writer;
    private int topK;

    private PriorityQueue<Recommendation> predictions;

    public TrustNetworkCell(String authorId, Map<String, List<Citation>> ratings, List<Similar> trusted, BufferedWriter writer, int topK) {
        this.authorId = authorId;
        this.ratings = ratings;
        this.trusted = trusted;
        this.writer = writer;
        this.topK = topK == -1 ? DEFAULT_TOPK : topK;
    }

    @Override
    public void run() {
        try {
            List<String> unrated = Commons.fetchUnratedArticles(authorId, ratings, trusted);

            if (unrated.isEmpty()) {
                LOG.debug("There is no item to predict for user " + authorId);
                return;
            }

            predictions = new PriorityQueue<>(topK);
            for (String itemId : unrated) {
                double prediction = predict(itemId);

                if (prediction != Commons.WRONG && Commons.canAdd(predictions, prediction, topK)) {
                    addPrediction(itemId, prediction);
                }
            }

            Commons.putOnFile(predictions, authorId, writer);
        } catch (Exception e) {
            LOG.error("Some error occurred, please check", e);
        }
    }

    private void addPrediction(String itemId, double prediction) {
        if (predictions.size() >= topK) {
            predictions.remove();
        }

        predictions.add(new Recommendation(itemId, prediction));
    }

    private double predict(String itemId) {
        double up = .0;
        double down = .0;

        for (Similar sim : trusted) {
            double simRate = Commons.getRating(ratings, sim.getAuthorId(), itemId);

            if (simRate != Commons.WRONG) {
                up += (sim.getScore() * simRate);
            }
            down += sim.getScore();
        }

        if (down == .0) {
            LOG.warn("The sum of similarities is 0, and it is weird: " + authorId + ", " + up + ", " + down + ", " + itemId);
            return Commons.WRONG;
        }

        return (up / down);
    }
}
