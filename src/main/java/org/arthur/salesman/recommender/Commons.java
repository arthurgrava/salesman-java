package org.arthur.salesman.recommender;

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
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Common methods between recommenders
 *
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.25
 */
public class Commons {

    private final Logger LOG = LogManager.getLogger(this);
    protected static final int WRONG = -999;

    /**
     * Gets the list of items that the {@param authorId} may be interested in because he didn't rate it yet
     *
     * @return
     */
    protected List<String> fetchUnratedArticles(String authorId, Map<String, List<Citation>> ratings, List<Similar> similars) {
        List<Citation> ratedItems = ratings.get(authorId);

        if (ratedItems == null) {
            ratedItems = Collections.emptyList();
            LOG.info("Author has no citations: " + authorId);
        } else if (similars == null) {
            LOG.error("No similar authors to this author, it is weird: " + authorId);
            return Collections.emptyList();
        }

        Set<String> unratedItems = new HashSet<>();

        LOG.info("fetch unrated items: " + authorId + ", " + ratedItems.size() + ", " + similars.size());

        for (Similar similar : similars) {
            String similarId = similar.getAuthorId();
            if (ratings.containsKey(similarId)) {
                List<Citation> similarRatings = ratings.get(similarId);
                LOG.info("Data: " + authorId + "=" + ratedItems.size() + ", " + similarId + "=" + similarRatings.size());
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
     * Returns true if the prediction must be added on the {@param predictions} queue
     *
     * @param predictions top k predictions queue
     * @param score       new item prediction score
     * @param topK        top k accepted on the predictions queue
     * @return true if we must insert the value, false otherwise
     */
    public static boolean canAdd(PriorityQueue<Recommendation> predictions, double score, int topK) {
        if (predictions.size() >= topK) {
            Recommendation lower = predictions.peek();

            return lower.getScore() < score;
        }
        return true;
    }

    /**
     * Appends the predicted values of this author to the target file
     *
     * @param predictions predictions for author
     * @param authorId    author id
     * @param writer      target file
     * @throws IOException
     */
    public void putOnFile(PriorityQueue<Recommendation> predictions, String authorId, BufferedWriter writer) throws IOException {
        if (writer != null) {
            for (Recommendation rec : predictions) {
                String line = authorId + "," + rec.getItemId() + "," + rec.getScore();
                writer.write(line + "\n");
                LOG.debug("PREDICTION: " + line);
            }
            writer.flush();
        } else {
            for (Recommendation rec : predictions) {
                System.out.println(authorId + "," + rec.getItemId() + "," + rec.getScore());
            }
        }
    }

    /**
     * Gets the rating of a similar user to {@link this.mainAuthor} on the specified item
     *
     * @param authorId Similar author
     * @param item     Item to be rated
     * @return
     */
    public double getRating(Map<String, List<Citation>> ratings, String authorId, String item) {
        List<Citation> temp = ratings.get(authorId);

        if (temp != null && !temp.isEmpty()) {
            for (Citation citation : temp) {
                if (item.equals(citation.getArticleId())) {
                    return citation.getScore();
                }
            }
        }

        return WRONG;
    }
}
