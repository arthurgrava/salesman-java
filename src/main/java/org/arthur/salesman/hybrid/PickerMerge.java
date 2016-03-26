package org.arthur.salesman.hybrid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.arthur.salesman.model.Recommendation;
import org.arthur.salesman.reader.RatingsReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class merges two algorithms results by picking answer first from the
 *
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.26
 */
public class PickerMerge implements Merge {

    private static final Logger LOG = LogManager.getLogger(PickerMerge.class);

    private Map<String, List<Recommendation>> recsA;
    private Map<String, List<Recommendation>> recsB;
    private String target;

    public PickerMerge(Map<String, List<Recommendation>> recsA, Map<String, List<Recommendation>> recsB, String target) {
        this.recsA = recsA;
        this.recsB = recsB;
        this.target = target;
    }

    public void doMagic() throws IOException {
        BufferedWriter bw = null;
        try {
            bw = target == null ? null : new BufferedWriter(new FileWriter(new File(target)));

            LOG.info("Starting merge");
            System.out.println("Starting merge");
            toFile(bw);
        } catch (IOException e) {
            LOG.error("Error while trying to access file -- " + e.getMessage(), e);
        } finally {
            if (bw != null) {
                bw.close();
            }
        }
    }

    private void toFile(BufferedWriter bw) throws IOException {
        Set<String> users = new HashSet<>(recsA.keySet());
        users.addAll(recsB.keySet());

        for (String user : users) {
            List<Recommendation> recommendations = recsA.containsKey(user) ? recsA.get(user) : recsB.get(user);
            if (recommendations != null) {
                for (Recommendation recommendation : recommendations) {
                    String line = user + "," + recommendation.getItemId() + "," + recommendation.getScore();
                    if (bw != null) {
                        bw.write(line + "\n");
                    }
                    LOG.debug("RECOMMENDATION: " + line);
                }
            }
        }
    }
}
