package org.arthur.salesman.reader;

import org.arthur.salesman.model.Similar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Arthur Grava (arthur at luizalabs.com).
 */
public class SimilarityReader {

    private static final String SEPARATOR = ",\\{";
    private static final Double DEFAULT = 0.0;

    private SimilarityReader() { }

    public static Map<String, List<Similar>> readFile(final String filePath) throws IOException {
        return readFile(filePath, SEPARATOR);
    }

    public static  Map<String, List<Similar>> readFile(final String filePath, final String separator) throws
                                                                                                       IOException {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(filePath));
            Map<String, List<Similar>> similarities = new HashMap<>();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] params = line.split(separator);
                if (params.length < 2) {
                    continue;
                }

                String authorId = params[0].replace("\"", "");
                List<Similar> similars = parseLine(params[1]);

                if (similarities.containsKey(authorId)) {
                    System.err.println("There was another author");
                } else {
                    similarities.put(authorId, similars);
                }
            }

            return similarities;
        } catch (IOException e) {
            throw e;
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
    }

    private static List<Similar> parseLine(String authors) {
        authors = authors.replace("}", "");

        String[] singles = authors.split(",");
        List<Similar> similarList = new ArrayList<>(singles.length);
        for (String single : singles) {
            String[] pair = single.split("\":");

            Similar similar = new Similar();

            try {
                similar.setAuthorId(pair[0].replace("\"", ""));
                similar.setScore(Double.parseDouble(pair[1].trim()));

                similarList.add(similar);
            } catch (Exception e) {
                System.err.println(e);
            }
        }

        return similarList;
    }

}
