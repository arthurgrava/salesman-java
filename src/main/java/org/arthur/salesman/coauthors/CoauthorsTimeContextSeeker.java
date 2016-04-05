package org.arthur.salesman.coauthors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arthur.salesman.model.Coauthorship;
import org.arthur.salesman.utils.Normalization;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.04.04
 */
public class CoauthorsTimeContextSeeker {

    private final Logger logger = LogManager.getLogger(CoauthorsTimeContextSeeker.class);

    // given on parameters
    private String publicationsPath;
    private String separator;
    private double ini;
    private double end;

    // locally initialized
    private Map<String, Set<Coauthorship>> publications;

    public CoauthorsTimeContextSeeker(String publicationsPath, String separator, int nAuthors, double ini, double end) {
        this.publicationsPath = publicationsPath;
        this.separator = separator;
        publications = new HashMap<>(nAuthors);
        this.ini = ini;
        this.end = end;
    }

    public Map<String, Map<String, Double>> calculate() throws IOException {
        populate();

        Map<String, Map<String, Double>> norm = new HashMap<>(publications.size());

        Set<String> keys = new HashSet<>(publications.keySet());
        for (String user : keys) {
            norm.put(user, normalizedValues(publications.remove(user)));
        }

        return norm;
    }

    private Map<String, Double> normalizedValues(Set<Coauthorship> coauthorships) {
        Map<String, Double> map = new HashMap<>(coauthorships.size());

        double max = Collections.max(coauthorships).getYear();
        double min = Collections.min(coauthorships).getYear();

        for (Coauthorship coauthorship : coauthorships) {
            double score = Normalization.feature(min, max, coauthorship.getYear(), ini, end);
            if (map.containsKey(coauthorship.getAuthorId())) {
                score += map.get(coauthorship.getAuthorId());
            }
            map.put(coauthorship.getAuthorId(), score);
        }

        return map;
    }

    public void populate() throws IOException {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(new File(publicationsPath)));

            String line;
            while ((line = br.readLine()) != null) {
                try {
                    String[] publication = line.split(separator);

                    String[] authors = publication[2].trim().toLowerCase().replace("\"", "").split(";");
                    int year = getYear(publication[4].trim().replace("\"", ""));

                    addCoauthors(authors, year);
                } catch (NumberFormatException e) {
                    logger.error("Error parsing line, year is not a number.\n\tline=" + line);
                }
            }
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    private void addCoauthors(String[] authors, int year) {
        for (int i = 0 ; i < authors.length ; i++) {
            String a = authors[i].trim();
            for (int j = i + 1 ; j < authors.length ; j++) {
                String b = authors[j].trim();
                addCoauthor(a, b, year);
                addCoauthor(b, a, year);
            }
        }
    }

    private void addCoauthor(String one, String two, int year) {
        if (!publications.containsKey(one)) {
            publications.put(one, new HashSet<Coauthorship>(30));
        }

        Set<Coauthorship> set = publications.get(one);
        set.add(new Coauthorship(two, year));
    }

    private int getYear(String year) {
        try {
            return Integer.parseInt(year);
        } catch (Exception e) {
            throw new NumberFormatException("Not a number");
        }
    }

    public Map<String, Set<Coauthorship>> getPublications() {
        return publications;
    }
}
