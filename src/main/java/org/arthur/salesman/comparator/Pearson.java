package org.arthur.salesman.comparator;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.arthur.salesman.model.Citation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Arthur Grava (arthur at luizalabs.com).
 */
public class Pearson {

    private static final Mean mean = new Mean();

    public static double compare(List<Citation> a, List<Citation> b) {
        Map<Integer, List<Citation>> map = intersection(a, b);

        List<Citation> commonA = map.get(0);
        List<Citation> commonB = map.get(1);

        if (commonA.size() == 0 || commonB.size() == 0) {
            return 0.0;
        }

        if (commonA.size() != commonB.size()) {
            throw new RuntimeException("The size of the common vectors are different!");
        }

        return correlation(toDouble(commonA), toDouble(commonB));
    }

    private static double correlation(double[] a, double[] b) {
        double meanA = mean.evaluate(a);
        double meanB = mean.evaluate(b);

        double up = 0.0;
        double d1 = 0.0, d2 = 0.0;
        for (int i = 0 ; i < a.length ; i++) {
            up += ((a[i] - meanA) * (b[i] - meanB));
            d1 += ((a[i] - meanA) * (a[i] - meanA));
            d2 += ((b[i] - meanB) * (b[i] - meanB));
        }

        if (d1 == 0.0 || d2 == 0.0) {
            return -1.0;
        } else {
            return up / Math.sqrt((d1 * d2));
        }
    }

    private static double[] toDouble(List<Citation> list) {
        double[] arry = new double[list.size()];
        for (int i = 0 ; i < list.size() ; i++) {
            arry[i] = list.get(i).getScore();
        }
        return arry;
    }

    private static Map<Integer, List<Citation>> intersection(List<Citation> a, List<Citation> b) {
        Map<Integer, List<Citation>> map = new HashMap<>();
        List<Citation> commonA = new ArrayList<>(Math.max(a.size(), b.size()));
        List<Citation> commonB = new ArrayList<>(Math.max(a.size(), b.size()));

        for (int i = 0 ; i < a.size() ; i++) {
            for (int j = 0 ; j < b.size() ; j++) {
                if (a.get(i).equals(b.get(j))) {
                    commonA.add(a.get(i));
                    commonB.add(b.get(j));
                }
            }
        }

        map.put(0, commonA);
        map.put(1, commonB);

        return map;
    }

}
