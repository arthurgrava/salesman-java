package org.arthur.salesman.runner;

import org.arthur.salesman.model.Citation;
import org.arthur.salesman.model.Similar;
import org.arthur.salesman.reader.CitationReader;
import org.arthur.salesman.reader.MeansReader;
import org.arthur.salesman.reader.SimilarityReader;
import org.arthur.salesman.recommender.Recommender;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Arthur Grava (arthur at luizalabs.com).
 */
public class RecommendationCalculator {

    public RecommendationCalculator() {

    }

    public void execute(String citationsPath, String similarsPath, String meansFile, String target) throws Exception {
        BufferedWriter bw = null;
        try {
            Map<String, Double> means = MeansReader.readFile(meansFile);
            Map<String, List<Similar>> similarities = SimilarityReader.readFile(similarsPath);
            Map<String, List<Citation>> citations = CitationReader.readFile(citationsPath);

            ThreadPoolExecutor tpe = new ThreadPoolExecutor(3, 3, 1, TimeUnit.MILLISECONDS, new ArrayBlockingQueue
                    <Runnable>(1));

            bw = new BufferedWriter(new FileWriter(target));

            for (String authorId : similarities.keySet()) {
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
                                new Recommender(
                                        authorId, citations, similarities.get(authorId), bw, means
                                )
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
        } catch(Exception e) {
            System.err.println(e);
        } finally {
            if (bw != null) {
                bw.close();
            }
        }
    }

    public static void main(String... args) throws Exception {
        new RecommendationCalculator().execute(
                "/home/arthur/work/data/normalized/citations_sample.csv",
                "/home/arthur/work/data/normalized/similars_1line_sample.csv",
                "/home/arthur/work/data/normalized/authors_means_24.csv",
                "/home/arthur/work/data/normalized/exec_sample_test.csv"
        );
    }

}
