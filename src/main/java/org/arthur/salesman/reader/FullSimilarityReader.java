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
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.12
 */
public class FullSimilarityReader {
    private static final String SEPARATOR = ",";

    private FullSimilarityReader() { }

    public static Map<String, List<Similar>> readFile(final String filePath) throws IOException {
        return readFile(filePath, SEPARATOR);
    }

    public static Map<String, List<Similar>> readFile(String filePath, String separator) throws IOException {
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
                String similarId = params[1].replace("\"", "");
                double score = Double.parseDouble(params[2]);

                if (!similarities.containsKey(authorId)) {
                    similarities.put(authorId, new ArrayList<Similar>(30));
                }

                Similar similar = getSimilar(similarId, score);

                List<Similar> similars = similarities.get(authorId);
                similars.add(similar);

                similarities.put(authorId, similars);
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

    private static Similar getSimilar(String similarId, double score) {
        Similar similar = new Similar();
        similar.setAuthorId(similarId);
        similar.setScore(score);
        return similar;
    }

}
