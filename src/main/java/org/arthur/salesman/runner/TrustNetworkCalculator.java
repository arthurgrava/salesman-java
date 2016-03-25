package org.arthur.salesman.runner;

import org.apache.commons.lang3.StringUtils;
import org.arthur.salesman.model.Citation;
import org.arthur.salesman.model.Similar;
import org.arthur.salesman.reader.CitationReader;
import org.arthur.salesman.reader.FullSimilarityReader;
import org.arthur.salesman.recommender.TrustNetworkCell;
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
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.25
 */
public class TrustNetworkCalculator {

    private String citationsPath;
    private String trustPath;
    private String authors;
    private String target;
    private int topK;

    private TrustNetworkCalculator(String citationsPath, String trustPath, String authors, String target, int topK) {
        this.citationsPath = citationsPath;
        this.trustPath = trustPath;
        this.authors = authors;
        this.target = target;
        this.topK = topK;
    }

    public void execute(final int coreSize, final int maxSize) throws IOException {
        BufferedWriter bw = null;
        BufferedReader br = null;
        try {
            Map<String, List<Similar>> similarities = FullSimilarityReader.readFile(trustPath);
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
                                new TrustNetworkCell(
                                        authorId, citations, similarities.get(authorId), bw, topK
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
        } catch (Exception e) {
            System.err.println(e);
        } finally {
            if (bw != null) {
                bw.close();
            }
            if (br != null) {
                br.close();
            }
        }
    }

    public static TrustNetworkCalculator getCalculator(Properties props) throws IOException {
        String citationsPath = props.getProperty("citations.path");
        String trustPath = props.getProperty("similarity.path");
        String targetPath = props.getProperty("target.path");
        String authorsPath = props.getProperty("authors.path");
        int topK = Integer.parseInt(props.getProperty("top.k", "-1"));

        if (StringUtils.isNoneBlank(citationsPath, trustPath, targetPath, authorsPath)) {
            System.out.println("Configurations are:\n\t" +
                    Strings.join("\n\t", citationsPath, trustPath, targetPath, authorsPath)
            );

            TrustNetworkCalculator tnc = new TrustNetworkCalculator(
                    citationsPath, trustPath, authorsPath, targetPath, topK
            );

            return tnc;
        } else {
            System.err.println("To run trustnet you need to specify citations.path, trust.path, and target.path\n");
            throw new IOException("missing parameters");
        }
    }
}
