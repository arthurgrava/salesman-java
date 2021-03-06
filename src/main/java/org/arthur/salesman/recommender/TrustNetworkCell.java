package org.arthur.salesman.recommender;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arthur.salesman.model.Citation;
import org.arthur.salesman.model.Recommendation;
import org.arthur.salesman.model.Similar;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.25
 */
public class TrustNetworkCell extends Commons implements Runnable {

    private static final Logger LOG = LogManager.getLogger(TrustNetworkCell.class);
    private static final int DEFAULT_TOPK = 50;

    private String authorId;
    private Map<String, List<Citation>> ratings;
    private List<Similar> trusted;
    private BufferedWriter writer;
    private int topK;
    private int topN;

    private PriorityQueue<Recommendation> predictions;

    public TrustNetworkCell(String authorId, Map<String, List<Citation>> ratings, List<Similar> trusted, BufferedWriter writer, int topK, int topN) {
        this.authorId = authorId;
        this.ratings = ratings;
        this.writer = writer;
        this.topK = topK == -1 ? DEFAULT_TOPK : topK;
        this.topN = topN;
        this.trusted = getTopNeighbors(trusted, topN);
    }

    private List<Similar> getTopNeighbors(List<Similar> similars, int topN) {
        if (similars == null) {
            return new ArrayList<>(1);
        }

        Collections.sort(similars);
        Collections.reverse(similars);

        return similars.size() > topN ? similars.subList(0, topN) : similars;
    }

    @Override
    public void run() {
        try {
            List<String> unrated = fetchUnratedArticles(authorId, ratings, trusted);

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

            putOnFile(predictions, authorId, writer);
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
            double simRate = getRating(ratings, sim.getAuthorId(), itemId);

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
