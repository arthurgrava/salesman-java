package org.arthur.salesman.runner;

import org.apache.commons.lang3.StringUtils;
import org.arthur.salesman.model.Citation;
import org.arthur.salesman.model.Similar;
import org.arthur.salesman.reader.CitationReader;
import org.arthur.salesman.reader.FullSimilarityReader;
import org.arthur.salesman.reader.MeansReader;
import org.arthur.salesman.recommender.SRSCell;
import org.arthur.salesman.utils.Strings;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
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
 * @author Arthur Grava (arthur at luizalabs.com).
 */
public class SRSRunner {

    private String citationsPath;
    private String coauthorsPath;
    private String meansFile;
    private String authors;
    private String target;
    private int topK;
    private int topN;

    public SRSRunner(String citationsPath, String coauthorsPath, String meansFile, String authors,
                     String target, int topK, int topN) {
        this.citationsPath = citationsPath;
        this.coauthorsPath = coauthorsPath;
        this.meansFile = meansFile;
        this.authors = authors;
        this.target = target;
        this.topK = topK;
        this.topN = topN;
    }

    public void execute(int coreSize, int maxSize) throws Exception {
        BufferedWriter bw = null;
        BufferedReader br = null;
        try {
            Map<String, Double> means = MeansReader.readFile(meansFile);
            Map<String, List<Similar>> similarities = FullSimilarityReader.readFile(coauthorsPath);
            Map<String, List<Citation>> citations = CitationReader.readFile(citationsPath);

            ThreadPoolExecutor tpe = new ThreadPoolExecutor(coreSize, maxSize, 1, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(1));

            bw = new BufferedWriter(new FileWriter(target));
            br = new BufferedReader(new FileReader(authors));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.contains("\"")) {
                    continue;
                }
                String authorId = line.replace("\"", "");
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
                                new SRSCell(
                                        authorId, citations, similarities.get(authorId), means, topK, bw, topN
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

    public static SRSRunner getCalculator(Properties props, boolean debug) throws IOException {
        String citations = props.getProperty("citations.path");
        String similarity = props.getProperty("similarity.path");
        String means = props.getProperty("means.path");
        String target = props.getProperty("target.path");
        String authors = props.getProperty("authors.path");

        int topK = Integer.parseInt(props.getProperty("top.k", "-1"));
        int topN = Integer.parseInt(props.getProperty("top.neighbors", "20"));

        if (StringUtils.isNoneBlank(citations, similarity, means, target, authors)) {
            System.out.println("Configurations are:\n\t" +
                               Strings.join("\n\t", citations, similarity, means, authors, target, debug)
            );

            return new SRSRunner(citations, similarity, means, authors, target, topK, topN);
        } else {
            System.err.println("To run usercf you need to specify citations.path, similarity.path, " +
                               "means.path and target.path");
            throw new IOException("missing parameters");
        }
    }

}
