package org.arthur.salesman.evaluation;

import org.arthur.salesman.model.Recommendation;
import org.arthur.salesman.model.Used;

import java.util.ArrayList;
import java.util.List;

/**
 * This class was created to fetch all predicted items and see if they were used as recommendation or not. So it will
 * check the <i>top k</i> recommendations and mark them as <i>predicted right</i> or <i>predicted wrongly</i>
 *
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.04.07
 */
public class Usefulness {

    private Usefulness() { }

    public static List<Used> evaluate(List<Recommendation> original, List<Recommendation> predicted) throws Exception {
        if (original == null || original.size() == 0 || predicted == null || predicted.size() == 0) {
            throw new Exception("Problem with the given data");
        }

        List<Used> list = new ArrayList<>(predicted.size());
        for (Recommendation prediction : predicted) {
            if (original.contains(prediction)) {
               list.add(new Used(prediction, 1));
            } else {
                list.add(new Used(prediction, 0));
            }
        }

        return list;
    }

}
