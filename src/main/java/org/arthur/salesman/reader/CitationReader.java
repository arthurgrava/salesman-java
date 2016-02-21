package org.arthur.salesman.reader;

import org.arthur.salesman.model.Citation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Arthur Grava (arthur at luizalabs.com).
 */
public class CitationReader {

    private static final String SEPARATOR = ",\\{";
    private static final Double DEFAULT = 0.0;

    public static  Map<String, List<Citation>> readFile(final String filePath) throws IOException {
        return readFile(filePath, SEPARATOR);
    }

    public static  Map<String, List<Citation>> readFile(final String filePath, final String separator) throws
                                                                                                     IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            Map<String, List<Citation>> ratings = new HashMap<String, List<Citation>>();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] params = line.split(separator);
                if (params.length < 2) {
                    continue;
                }

                String authorId = params[0].replace("\"", "");
                List<Citation> citations = parseLine(authorId, params[1]);

                if (ratings.containsKey(authorId)) {
                    System.err.println("There was another author");
                } else {
                    ratings.put(authorId, citations);
                }
            }

            return ratings;
        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            throw e;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private static List<Citation> parseLine(String authorId, String ratings) {
        ratings = ratings.replace("}", "");

        String[] singles = ratings.split(",");
        List<Citation> citations = new ArrayList<Citation>(singles.length);
        for (String single : singles) {
            String[] pair = single.split("\":");

            Citation citation = new Citation();

            try {
                citation.setAuthorId(authorId);
                citation.setArticleId(pair[0].replace("\"", ""));
                if (pair.length == 2) {
                    citation.setScore(Double.parseDouble(pair[1].trim()));
                } else {
                    citation.setScore(DEFAULT);
                }

                citations.add(citation);
            } catch (Exception e) {
                System.err.println(e);
            }
        }

        return citations;
    }

}
