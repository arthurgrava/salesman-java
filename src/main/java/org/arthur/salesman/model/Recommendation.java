package org.arthur.salesman.model;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Arthur Grava (arthur at luizalabs.com).
 */
public class Recommendation implements Comparable<Recommendation> {
    private String itemId;
    private double score;

    public Recommendation() {
    }

    public Recommendation(String itemId, double score) {
        this.itemId = itemId;
        this.score = score;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    /**
     * Compares the objects using the variable {@code score}
     *
     * @param other
     * @return
     * <ul>
     *     <li>1 if the score of the other object is higher</li>
     *     <li>0 if the score of the other object is equal</li>
     *     <li>-1 if the score of the other object is lower</li>
     * </ul>
     */
    @Override
    public int compareTo(Recommendation other) {
        if (this.score > other.score) {
            return 1;
        } else if (this.score < other.score) {
            return -1;
        } else {
            return 0;
        }
    }

    private boolean equals(Recommendation rec) {
        return StringUtils.isNotBlank(this.itemId) && this.itemId.equals(rec.itemId);
    }

    @Override
    public boolean equals(Object obj) {
        return equals((Recommendation) obj);
    }
}
