package org.arthur.salesman.evaluation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arthur.salesman.model.Recommendation;
import org.arthur.salesman.utils.Doubles;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

/**
 * This class is responsible for taking two lists of item/score pair and calculate <i>RMSE</i>, <i>MAE</i> and <i>S@K
 * </i>. One list is the list of recommendations and the other list is the list of items rated and removed from the
 * training dataset.
 *
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.13
 */
public class EvalCell implements Runnable {

    private List<Recommendation> predictions;
    private List<Recommendation> ratings;
    private String authorId;
    private BufferedWriter target;

    protected double sAtK;
    protected double mae;
    protected double rmse;

    private static final Logger LOG = LogManager.getLogger(EvalCell.class);

    public EvalCell(String authorId, List<Recommendation> predictions, List<Recommendation> ratings, BufferedWriter bw) {
        this.authorId = authorId;
        this.predictions = predictions;
        this.ratings = ratings;
        this.target = bw;
    }

    @Override
    public void run() {
        try {
            this.sAtK = ScoreAtK.evaluate(this.ratings, this.predictions);

            this.ratings.retainAll(this.predictions);

            double[] dRatings = new double[predictions.size()];
            double[] dPredictions = new double[predictions.size()];

            for (int i = 0 ; i < predictions.size() ; i++) {
                Recommendation recommendation = predictions.get(i);
                dPredictions[i] = recommendation.getScore();

                if (ratings.contains(recommendation)) {
                    dRatings[i] = ratings.get(ratings.indexOf(recommendation)).getScore();
                } else {
                    dRatings[i] = .0;
                }
            }

            this.mae = Mae.evaluate(dRatings, dPredictions);
            this.rmse = Rmse.evaluate(dRatings, dPredictions);

            sendResultsToFile();
        } catch (Exception e) {
            LOG.error("Some error occured: " + e.getMessage(), e);
        }
    }

    private void sendResultsToFile() {
        if (target != null) {
            try {
                String line = authorId + "," + Doubles.round(sAtK, 5) + "," + Doubles.round(mae, 5) + "," + Doubles.round(rmse, 5);
                target.write(line + "\n");
                target.flush();
                LOG.debug("Evaluation was: " + line);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
