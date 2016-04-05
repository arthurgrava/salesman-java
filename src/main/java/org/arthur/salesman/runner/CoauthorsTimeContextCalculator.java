package org.arthur.salesman.runner;

import org.apache.commons.lang3.StringUtils;
import org.arthur.salesman.coauthors.CoauthorsTimeContextSeeker;
import org.arthur.salesman.model.Similar;
import org.arthur.salesman.utils.Strings;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Properties;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.04.05
 */
public class CoauthorsTimeContextCalculator {

    private static final String DEFAULT_SEPARATOR = ",";
    private static final int DEFAULT_TOPK = 50;

    private String source;
    private String target;
    private double normalizationIni;
    private double normalizationEnd;
    private int nAuthorsHint;
    private String separator;

    private CoauthorsTimeContextCalculator(String source, String target, double normalizationIni, double normalizationEnd, int nAuthorsHint, String separator) {
        this.source = source;
        this.target = target;
        this.normalizationIni = normalizationIni;
        this.normalizationEnd = normalizationEnd;
        this.nAuthorsHint = nAuthorsHint;
        this.separator = separator;
    }

    public static CoauthorsTimeContextCalculator getCalculator(Properties props) throws IOException {
        String ratingsPath = props.getProperty("ratings.path");
        String targetPath = props.getProperty("target.path");
        double normalizationIni = Double.parseDouble(props.getProperty("range.normalization.ini", ".25"));
        double normalizationEnd = Double.parseDouble(props.getProperty("range.normalization.end", "1.0"));
        int nAuthorsHint = Integer.parseInt(props.getProperty("authors.quantity", "275000"));
        String separator = props.getProperty("file.separator", ",");

        if (StringUtils.isNoneBlank(ratingsPath, targetPath)) {
            System.out.println(
                    "Configurations are:\n\t" +
                            Strings.join("\n\t", ratingsPath, targetPath, normalizationIni, normalizationEnd, nAuthorsHint, separator)
            );
            return new CoauthorsTimeContextCalculator(
                    ratingsPath, targetPath, normalizationIni, normalizationEnd, nAuthorsHint, separator
            );
        } else {
            System.out.println("Your configuration file must have:");
            System.out.println("  * ratings.path\t--  Testing dataset");
            System.out.println("  * target.path\t\t--  Target of coauthorship result\n\n");
            throw new IOException("Problem with your parameters");
        }
    }

    public void execute() throws IOException {
        CoauthorsTimeContextSeeker seeker = new CoauthorsTimeContextSeeker(source, separator, nAuthorsHint, normalizationIni, normalizationEnd);

        System.out.println("Starting the calculation");
        Map<String, Map<String, Double>> res = seeker.calculate();

        System.out.println("Starting to save on file");
        printToFile(res);

        System.out.println("Saved on file");
    }

    private void printToFile(Map<String, Map<String, Double>> res) throws IOException {
        BufferedWriter bw = null;

        try {
            bw = new BufferedWriter(new FileWriter(target));

            for (String author : res.keySet()) {
                PriorityQueue<Similar> topk = new PriorityQueue<>(DEFAULT_TOPK);

                for (String coauthor : res.get(author).keySet()) {
                    double score = res.get(author).get(coauthor);

                    if (topk.size() >= DEFAULT_TOPK) {
                        Similar tmp = topk.peek();

                        if (tmp.getScore() < score) {
                            topk.remove();
                            topk.add(new Similar(coauthor, score));
                        }
                    } else {
                        topk.add(new Similar(coauthor, score));
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
}
