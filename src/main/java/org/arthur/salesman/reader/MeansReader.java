package org.arthur.salesman.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.07
 */
public class MeansReader {

    private static final String SPLITTER = ",";

    public static Map<String, Double> readFile(String path) throws IOException {
        return readFile(SPLITTER, path);
    }

    public static Map<String, Double> readFile(final String splitter, final String path) throws IOException {
        Map<String, Double> map = new HashMap<>();

        BufferedReader bw = null;
        try {
            bw = new BufferedReader(new FileReader(new File(path)));
            String line;
            while ((line = bw.readLine()) != null) {
                String[] params = line.split(splitter);

                if (params.length < 2) {
                    continue;
                }

                try {
                    map.put(
                        params[0].trim().toLowerCase().replace("\"", ""),
                        Double.parseDouble(params[1])
                    );
                } catch (Exception e) {
                    System.err.println("Could no parse line: " + line);
                }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (bw != null) {
                bw.close();
            }
        }

        return map.size() == 0 ? null : map;
    }

}
