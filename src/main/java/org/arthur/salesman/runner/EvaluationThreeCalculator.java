package org.arthur.salesman.runner;

import org.apache.commons.lang3.StringUtils;
import org.arthur.salesman.evaluation.Evaluation;
import org.arthur.salesman.model.Recommendation;
import org.arthur.salesman.reader.RatingsReader;
import org.arthur.salesman.utils.Strings;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.04.11
 */
public class EvaluationThreeCalculator {

    private String testRatingsPath;
    private String predictionsPath;
    private String targetPath;

    private EvaluationThreeCalculator(String testRatingsPath, String predictionsPath, String targetPath) {
        this.testRatingsPath = testRatingsPath;
        this.predictionsPath = predictionsPath;
        this.targetPath = targetPath;
    }

    public void execute(int coreSize, int maxSize) throws IOException {
        BufferedWriter bw = null;
        try {
            Map<String, List<Recommendation>> realRatings = RatingsReader.readFile(testRatingsPath);
            Map<String, List<Recommendation>> predictions = RatingsReader.readFile(predictionsPath);

            String infos = "Test set " + realRatings.keySet().size() + "\nPrediction set " + predictions.keySet().size();
            System.out.println("\n" + infos + "\n");

            bw = new BufferedWriter(new FileWriter(new File(targetPath)));

            ThreadPoolExecutor tpe = new ThreadPoolExecutor(
                    coreSize, maxSize, 1, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1)
            );

            for (String userId : predictions.keySet()) {
                boolean running = false;
                while (!running) {
                    try {
                        int n = tpe.getMaximumPoolSize() - tpe.getActiveCount();
                        if (n <= 1) {
                            Thread.sleep(50);
                            continue;
                        }

                        // calculates the recommendations
                        tpe.execute(
                                new Evaluation(predictions.get(userId), realRatings.get(userId), userId, bw)
                        );
                        running = true;
                    } catch (RejectedExecutionException e) {
                        // do nothing
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            while (tpe.getActiveCount() > 1) {
                Thread.sleep(100);
            }

            tpe.shutdown();
            tpe.awaitTermination(100, TimeUnit.MILLISECONDS);

            while (!tpe.isTerminated()) {
                Thread.sleep(100);
            }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            if (bw != null) {
                bw.close();
            }
        }
    }

    public static EvaluationThreeCalculator getCalculator(final Properties props) throws IOException {
        String testRatingsPath = props.getProperty("ratings.path");
        String predictionsPath = props.getProperty("predictions.path");
        String targetPath = props.getProperty("target.path");

        if (StringUtils.isNoneBlank(testRatingsPath, predictionsPath, targetPath)) {
            System.out.println(
                    "Configurations are:\n\t" +
                            Strings.join("\n\t", testRatingsPath, predictionsPath, targetPath)
            );

            return new EvaluationThreeCalculator(testRatingsPath, predictionsPath, targetPath);
        } else {
            System.out.println("Your configuration file must have:");
            System.out.println("  * ratings.path\t--  Testing dataset");
            System.out.println("  * predictions.path\t--  Predicted dataset");
            System.out.println("  * target.path\t\t--  Target of evaluation result\n\n");
            throw new IOException("Missing parameters on the config file");
        }
    }

}
