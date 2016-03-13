package org.arthur.salesman.runner;

import org.apache.commons.lang3.StringUtils;
import org.arthur.salesman.model.Citation;
import org.arthur.salesman.model.Similar;
import org.arthur.salesman.reader.CitationReader;
import org.arthur.salesman.reader.FullSimilarityReader;
import org.arthur.salesman.reader.MeansReader;
import org.arthur.salesman.recommender.UserBasedCell;
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
public class UserBasedCalculator {

    private boolean debug;
    private int coreSize;
    private int maxSize;
    private String citationsPath;
    private String similarsPath;
    private String meansFile;
    private String authors;
    private String target;
    private int topK;

    public UserBasedCalculator(boolean debug, int coreSize, int maxSize, String citationsPath,
                               String similarsPath, String meansFile, String authors, String target, int topK) {
        this.debug = debug;
        this.coreSize = coreSize;
        this.maxSize = maxSize;
        this.citationsPath = citationsPath;
        this.similarsPath = similarsPath;
        this.meansFile = meansFile;
        this.authors = authors;
        this.target = target;
        this.topK = topK;
    }

    public void execute() throws Exception {
        BufferedWriter bw = null;
        BufferedReader br = null;
        try {
            Map<String, Double> means = MeansReader.readFile(meansFile);
            Map<String, List<Similar>> similarities = FullSimilarityReader.readFile(similarsPath);
            Map<String, List<Citation>> citations = CitationReader.readFile(citationsPath);

            ThreadPoolExecutor tpe = new ThreadPoolExecutor(coreSize, maxSize, 1, TimeUnit.MILLISECONDS, new ArrayBlockingQueue
                    <Runnable>(1));

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
                                new UserBasedCell(
                                        authorId, citations, similarities.get(authorId), bw, means, topK
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

    public static UserBasedCalculator getCalculator(Properties props, boolean debug) throws IOException {
        String citations = props.getProperty("citations.path");
        String similarity = props.getProperty("similarity.path");
        String means = props.getProperty("means.path");
        String target = props.getProperty("target.path");
        String authors = props.getProperty("authors.path");

        int coreThreads = Integer.parseInt(props.getProperty("core.size"));
        int maxThreads = Integer.parseInt(props.getProperty("max.size"));
        int topK = Integer.parseInt(props.getProperty("top.k", "-1"));

        if (StringUtils.isNoneBlank(citations, similarity, means, target, authors)) {
            System.out.println("Configurations are:\n\t" +
                    Strings.join("\n\t", citations, similarity, means, authors, target, coreThreads, maxThreads, debug)
            );

            UserBasedCalculator rc = new UserBasedCalculator(
                    debug, coreThreads, maxThreads, citations, similarity, means, authors, target, topK
            );

            return rc;
        } else {
            System.err.println("To run usercf you need to specify citations.path, similarity.path, " +
                    "means.path and target.path");
            throw new IOException("missing parameters");
        }
    }

    public static void main(String... args) throws Exception {
        new UserBasedCalculator(
                true, 3, 3,
                "/home/arthur/work/data/normalized/citations_sample.csv",
                "/home/arthur/work/data/normalized/similars_1line_sample.csv",
                "/home/arthur/work/data/normalized/authors_means_24.csv",
                "/home/arthur/work/data/normalized/authors_to_recommend.csv",
                "/home/arthur/work/data/normalized/exec_sample_test.csv",
                -1
        ).execute();
    }

}
