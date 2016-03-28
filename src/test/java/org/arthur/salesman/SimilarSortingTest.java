package org.arthur.salesman;

import org.arthur.salesman.model.Similar;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.03.27
 */
public class SimilarSortingTest {

    private static final int TOP_N = 10;

    @Test
    public void testSortingWithCustomComparator() {
        List<Similar> list = new ArrayList<Similar>(){{
            for (int i = 1 ; i <= 50 ; i++) {
                add(new Similar(i + "", Math.random()));
            }
        }};

        Collections.sort(list, new Comparator<Similar>() {
            @Override
            public int compare(Similar s1, Similar s2) {
                if (s1.getScore() < s2.getScore()) {
                    return 1;
                } else if (s1.getScore() > s2.getScore()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });

        Assert.assertEquals(max(list).getScore(), list.get(0).getScore(), .000001);
    }

    @Test
    public void testSortingPurely() {
        List<Similar> list = new ArrayList<Similar>(){{
            for (int i = 1 ; i <= 50 ; i++) {
                add(new Similar(i + "", Math.random()));
            }
        }};

        Collections.sort(list);
        Collections.reverse(list);
        
        list = list.size() > TOP_N ? list.subList(0, TOP_N) : list;

        Assert.assertEquals(max(list).getScore(), list.get(0).getScore(), .000001);
    }

    private Similar max(List<Similar> sim) {
        return Collections.max(sim);
    }
}
