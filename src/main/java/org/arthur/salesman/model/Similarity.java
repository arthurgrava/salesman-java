package org.arthur.salesman.model;

/**
 * @author Arthur Grava (arthur at luizalabs.com).
 */
public class Similarity implements Comparable<Similarity> {

    private String mainAuthor;
    private String comparedAuthor;
    private double score;

    public Similarity() { }

    public Similarity(String a, String b, double s) {
        this.mainAuthor = a;
        this.comparedAuthor = b;
        this.score = s;
    }

    public String getMainAuthor() {
        return mainAuthor;
    }

    public void setMainAuthor(String mainAuthor) {
        this.mainAuthor = mainAuthor;
    }

    public String getComparedAuthor() {
        return comparedAuthor;
    }

    public void setComparedAuthor(String comparedAuthor) {
        this.comparedAuthor = comparedAuthor;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "{" + this.mainAuthor + "," + this.comparedAuthor + "," + this.score + "}";
    }

    public String toCsv() {
        return this.mainAuthor + "," + this.comparedAuthor + "," + this.score;
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
    public int compareTo(Similarity other) {
        if (this.score > other.score) {
            return 1;
        } else if (this.score < other.score) {
            return -1;
        } else {
            return 0;
        }
    }
}
