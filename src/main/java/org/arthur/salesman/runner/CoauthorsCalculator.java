package org.arthur.salesman.runner;

import org.apache.commons.lang3.StringUtils;
import org.arthur.salesman.coauthors.CoauthorsSeeker;
import org.arthur.salesman.model.Similar;
import org.arthur.salesman.utils.Strings;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.Set;

/**
 * Created by tutu on 2/13/16.
 */
public class CoauthorsCalculator {

    private static final String DEFAULT_SEPARATOR = ",";
    private static final int DEFAULT_TOPK = 50;

    private String publicationsPath;
    private int nAuthors;
    private int nArticles;
    private String coauthorsPath;
    private String separator;
    private boolean debug;

    public CoauthorsCalculator(String publicationsPath, int nAuthors, int nArticles, String coauthorsPath, String separator, boolean debug) {
        this.publicationsPath = publicationsPath;
        this.nAuthors = nAuthors;
        this.nArticles = nArticles;
        this.coauthorsPath = coauthorsPath;
        this.separator = separator;
        this.debug = debug;
    }

    public void printToFile(Map<String, Map<String, Integer>> coauthors) throws IOException {
        BufferedWriter bw = null;

        try {
            bw = new BufferedWriter(new FileWriter(this.coauthorsPath));
            for (String author : coauthors.keySet()) {
                PriorityQueue<Similar> topk = new PriorityQueue<>(DEFAULT_TOPK);

                for (String coauthor : coauthors.get(author).keySet()) {
                    int score = coauthors.get(author).get(coauthor);

                    if (topk.size() >= DEFAULT_TOPK) {
                        Similar tmp = topk.peek();

                        if (tmp.getScore() < score) {
                            topk.remove();
                            topk.add(new Similar(coauthor, Double.parseDouble(score + "")));
                        }
                    } else {
                        topk.add(new Similar(coauthor, Double.parseDouble(score + "")));
                    }
                }

                for (Similar coauthor : topk) {
                    bw.write(author + DEFAULT_SEPARATOR + coauthor.getAuthorId() + DEFAULT_SEPARATOR + coauthor.getScore());
                    bw.newLine();
                }
                bw.flush();
            }
        } catch (IOException e) {
            System.err.println(e);
            throw e;
        } finally {
            if (bw != null) {
                bw.close();
            }
        }
    }

    public static CoauthorsCalculator getCalculator(Properties props) throws IOException {
        String ratings = props.getProperty("ratings.path");
        String coauthors = props.getProperty("target.path");
        boolean debug = Boolean.getBoolean(props.getProperty("debug", "true"));

        if (StringUtils.isNoneBlank(ratings, coauthors)) {
            System.out.println(
                    "Configurations are:\n\t" +
                            Strings.join("\n\t", ratings, coauthors)
            );
            return new CoauthorsCalculator(ratings, 10000, 10000, coauthors, ",", debug);
        } else {
            System.out.println("Your configuration file must have:");
            System.out.println("  * ratings.path\t--  Testing dataset");
            System.out.println("  * target.path\t\t--  Target of coauthorship result\n\n");
            throw new IOException("Missing parameters on the config file");
        }
    }

    public void execute() throws IOException {
        CoauthorsSeeker cs = new CoauthorsSeeker(publicationsPath, nAuthors, nArticles, separator);
        System.out.println("Starting the calculation");
        cs.calculate(this.debug);
        System.out.println("It will save on file now");
        Map<String, Map<String, Integer>> coauthors = cs.getCoauthorship();
        System.out.println("Starting to save on file");
        printToFile(coauthors);
        System.out.println("Saved on file");
    }
}
