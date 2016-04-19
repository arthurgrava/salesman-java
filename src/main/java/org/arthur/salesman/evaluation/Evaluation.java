package org.arthur.salesman.evaluation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arthur.salesman.model.Recommendation;
import org.arthur.salesman.utils.Doubles;
import org.arthur.salesman.utils.Strings;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Evaluates a group of item recommendations for some user
 *
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.26
 */
public class Evaluation implements Runnable {

    protected List<Recommendation> predictions;
    protected List<Recommendation> ratings;
    protected String authorId;
    protected BufferedWriter target;

    protected double[] sAtK;
    protected double[] recall;
    protected double[] precision;
    protected double[] mrr;

    private static final Logger LOG = LogManager.getLogger(Evaluation.class);
    private static final List<Recommendation> DEFAULT_PREDICTIONS = new ArrayList<Recommendation>(1){{
        add(new Recommendation("-99999", 1.0));
    }};

    public Evaluation(List<Recommendation> predictions, List<Recommendation> ratings, String authorId, BufferedWriter target) {
        this.predictions = predictions != null ? predictions : new ArrayList<>(DEFAULT_PREDICTIONS);
        this.ratings = ratings;
        this.authorId = authorId;
        this.target = target;
    }

    @Override
    public void run() {
        try {
            int size = 5;

            Collections.sort(predictions);

            if (predictions.size() > 1) {
                Collections.reverse(predictions);
            }

            sAtK = new double[size];
            recall = new double[size];
            precision = new double[size];
            mrr = new double[size];

            for (int i = 0 ; i < size ; i++) {
                int k = (i + 1) * size;
                List<Recommendation> predictionsTmp = sublist(predictions, k);

                sAtK[i] = ScoreAtK.evaluate(ratings, predictionsTmp);
                mrr[i] = Mrr.evaluate(ratings, predictionsTmp);
                recall[i] = Recall2.evaluate(ratings, predictionsTmp);
                precision[i] = Precision2.evaluate(recall[i], k);
            }

            putOnFile();
        } catch (Exception e) {
            LOG.error("Some error occurred while calculating result -- " + e.getMessage(), e);
        }
    }

    private void putOnFile() {
        if (target != null) {
            try {
                String sSatk = "", sPrecision = "", sRecall = "", sMrr = "";
                for (int i = 0; i < sAtK.length; i++) {
                    sSatk += Doubles.round(sAtK[i], 5);
                    sPrecision += Doubles.round(precision[i], 5);
                    sRecall += Doubles.round(recall[i], 5);
                    sMrr += Doubles.round(mrr[i], 5);
                    if (i != sAtK.length - 1) {
                        sSatk += ",";
                        sPrecision += ",";
                        sRecall += ",";
                        sMrr += ",";
                    }
                }

                String line = Strings.join(",", authorId, sSatk, sRecall, sPrecision, sMrr);

                target.write(line + "\n");
                target.flush();
                LOG.debug("Evaluation was: " + line);
            } catch (IOException e) {
                LOG.error(e.getMessage(), e);
            }
        }
    }

    private List<Recommendation> sublist(List<Recommendation> list, int end) {
        return end >= list.size() ? list : list.subList(0, end);
    }
}
