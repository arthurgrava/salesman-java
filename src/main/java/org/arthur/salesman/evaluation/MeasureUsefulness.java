package org.arthur.salesman.evaluation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arthur.salesman.model.Recommendation;
import org.arthur.salesman.model.Used;
import org.arthur.salesman.utils.Strings;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.04.07
 */
public class MeasureUsefulness implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger(MeasureUsefulness.class);
    private static final List<Recommendation> DEFAULT_PREDICTIONS = new ArrayList<Recommendation>(1){{
        add(new Recommendation("-99999", 1.0));
    }};

    private String authorId;
    private List<Recommendation> original;
    private List<Recommendation> predicted;
    private BufferedWriter target;

    public MeasureUsefulness(String authorId, List<Recommendation> original, List<Recommendation> predicted, BufferedWriter target) {

        this.authorId = authorId;
        this.original = original;
        this.predicted = predicted != null ? predicted : new ArrayList<>(DEFAULT_PREDICTIONS);
        this.target = target;
    }

    public void run() {
        try {
            List<Used> useful = Usefulness.evaluate(original, predicted);
            writeToFile(useful);
        } catch (Exception e) {
            LOGGER.error("Some error occurred, analyse stack trace.", e);
        }
    }

    private void writeToFile(List<Used> useful) throws IOException {
        if (target != null) {
            for (Used used : useful) {
                Recommendation rec = used.getRecommendation();
                target.write(Strings.join(",", authorId, rec.getItemId(), rec.getScore(), used.getUseful()) + "\n");
            }
        } else {
            for (Used used : useful) {
                Recommendation rec = used.getRecommendation();
                System.out.println(
                        Strings.join(",", authorId, rec.getItemId(), rec.getScore(), used.getUseful()) + "\n"
                );
            }
        }
    }
}
