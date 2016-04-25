package org.arthur.salesman.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arthur.salesman.model.Citation;
import org.arthur.salesman.model.Recommendation;
import org.arthur.salesman.model.Similar;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * @author Arthur Grava (arthur at luizalabs.com).
 */
public class RecommenderUtils {

    private RecommenderUtils() { }

    private static final Logger LOG = LogManager.getLogger(RecommenderUtils.class);
    public static final double WRONG = -999;

    public static List<Similar> getTopNeighbors(List<Similar> neighbors, int topN) {
        if (neighbors == null || neighbors.isEmpty()) {
            return new ArrayList<>(1);
        }

        Collections.sort(neighbors);

        if (neighbors.size() > 1) {
            double s1 = neighbors.get(0).getScore();
            double s2 = neighbors.get(1).getScore();

            if (s2 > s1) {
                Collections.reverse(neighbors);
            }
        }

        return neighbors.size() > topN ? neighbors.subList(0, topN) : neighbors;
    }

    /**
     * Gets the list of items that the {@param author} may be interested in because he didn't rate it yet
     *
     * @return
     */
    public static List<String> fetchUnratedArticles(String author, Map<String, List<Citation>> ratings, List<Similar>
            neighbors) {
        List<Citation> ratedItems = ratings.get(author);

        if (neighbors == null || neighbors.isEmpty()) {
            LOG.error("No similar authors to this author, it is weird: " + author);
            return Collections.emptyList();
        } else if (ratedItems == null) {
            LOG.info("Author has no citations: " + author);
            ratedItems = Collections.emptyList();
        }

        Set<String> unratedItems = new HashSet<>();

        LOG.debug("fetch unrated items: " + author + ", " + ratedItems.size() + ", " + neighbors.size());

        for (Similar neighbor : neighbors) {
            String neighborId = neighbor.getAuthorId();
            if (ratings.containsKey(neighborId)) {
                List<Citation> neighborRatings = ratings.get(neighborId);
                for (Citation citation : neighborRatings) {
                    if (!ratedItems.contains(citation)) {
                        unratedItems.add(citation.getArticleId());
                    }
                }
                LOG.info("Data: " + author + "=" + ratedItems.size() + ", " + neighborId + "=" + neighborRatings.size());
            }
        }

        return new ArrayList<>(unratedItems);
    }

    /**
     *
     * @param predictions
     * @throws IOException
     */
    public static void toFile(BufferedWriter writer, Queue<Recommendation> predictions, String author) throws IOException {
        if (writer != null) {
            for (Recommendation rec : predictions) {
                String line = author + "," + rec.getItemId() + "," + rec.getScore() + "\n";
                writer.write(line);
                LOG.debug("prediction: " + line);
            }
            writer.flush();
        } else {
            for (Recommendation rec : predictions) {
                System.out.println(author + "," + rec.getItemId() + "," + rec.getScore());
            }
        }
    }
}
