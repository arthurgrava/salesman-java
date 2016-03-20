package org.arthur.salesman.evaluation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arthur.salesman.model.Recommendation;
import org.arthur.salesman.utils.Doubles;
import org.arthur.salesman.utils.Strings;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
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

    protected double[] sAtK;
    protected double[] mae;
    protected double[] rmse;
    protected double[] mrr;

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
            int size = 5;

            sAtK = new double[size];
            mae = new double[size];
            rmse = new double[size];
            mrr = new double[size];

            // generates rmse and mae lists
            List<Recommendation> ratingsCopy = new ArrayList<>(this.ratings);
            ratingsCopy.retainAll(this.predictions);
            double[] dRatings = new double[predictions.size()];
            double[] dPredictions = new double[predictions.size()];
            for (int j = 0; j < predictions.size(); j++) {
                Recommendation recommendation = predictions.get(j);
                dPredictions[j] = recommendation.getScore();

                if (ratings.contains(recommendation)) {
                    dRatings[j] = ratings.get(ratings.indexOf(recommendation)).getScore();
                } else {
                    dRatings[j] = 1.0;
                }
            }

            for (int i = 0; i < 5; i++) {
                int k = (i + 1) * size;
                List<Recommendation> ratingsTmp = sublist(ratings, k);
                List<Recommendation> predictionsTmp = sublist(predictions, k);
                this.sAtK[i] = ScoreAtK.evaluate(ratingsTmp, predictionsTmp);
                this.mrr[i] = Mrr.evaluate(ratingsTmp, predictionsTmp);

                double[] dRatingsTmp = subarray(dRatings, k);
                double[] dPredictionsTmp = subarray(dPredictions, k);
                this.mae[i] = Mae.evaluate(dRatingsTmp, dPredictionsTmp);
                this.rmse[i] = Rmse.evaluate(dRatingsTmp, dPredictionsTmp);
            }
            sendResultsToFile();
        } catch (Exception e) {
            LOG.error("Some error occurred: " + e.getMessage(), e);
        }
    }

    private List<Recommendation> sublist(List<Recommendation> list, int end) {
        return end >= list.size() ? list : list.subList(0, end);
    }

    private static double[] subarray(double[] array, int end) {
        if (end >= array.length) {
            return array;
        }

        double[] tmp = new double[end];
        System.arraycopy(array, 0, tmp, 0, end);
        return tmp;
    }

    private void sendResultsToFile() {
        if (target != null) {
            try {
                String sSatk = "", sRmse = "", sMae = "", sMrr = "";
                for (int i = 0; i < mae.length; i++) {
                    sSatk += Doubles.round(sAtK[i], 5);
                    sRmse += Doubles.round(rmse[i], 5);
                    sMae += Doubles.round(mae[i], 5);
                    sMrr += Doubles.round(mrr[i], 5);
                    if (i != mae.length - 1) {
                        sSatk += ",";
                        sRmse += ",";
                        sMae += ",";
                        sMrr += ",";
                    }
                }

                String line = Strings.join(",", authorId, sSatk, sMae, sRmse, sMrr);

                target.write(line + "\n");
                target.flush();
                LOG.debug("Evaluation was: " + line);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }
}
