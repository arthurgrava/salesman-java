package org.arthur.salesman.reader;

import org.arthur.salesman.model.Recommendation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.13
 */
public class RatingsReader {
    private static final String DEFAULT_SEPARATOR = ",";

    public static Map<String, List<Recommendation>> readFile(String path) throws IOException {
        return readFile(path, DEFAULT_SEPARATOR);
    }

    private static Map<String, List<Recommendation>> readFile(String path, String separator) throws IOException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(new File(path)));
            Map<String, List<Recommendation>> ratings = new HashMap<>(10000);

            String line;
            while ((line = br.readLine()) != null) {
                String[] params = line.split(separator);
                if (params.length < 2) {
                    continue;
                }

                try {
                    String userId = params[0].replace("\"", "");
                    String itemId = params[1].replace("\"", "");
                    double score = Double.parseDouble(params[2]);

                    if (!ratings.containsKey(userId)) {
                        ratings.put(userId, new ArrayList<Recommendation>());
                    }

                    List<Recommendation> userRatings = ratings.get(userId);
                    userRatings.add(new Recommendation(itemId, score));
                    ratings.put(userId, userRatings);
                } catch (Exception e) { }
            }

            return ratings;
        } catch (IOException e) {
            throw e;
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

}
