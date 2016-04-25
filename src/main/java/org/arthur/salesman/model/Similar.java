package org.arthur.salesman.model;

/**
 * A score given to its similar author
 *
 * @author Arthur Grava (arthur at luizalabs.com).
 */
public class Similar implements Comparable<Similar> {

    private String authorId;
    private Double score;

    public Similar() {
    }

    public Similar(String authorId, Double score) {
        this.authorId = authorId;
        this.score = score;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
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
    public int compareTo(Similar other) {
        if (this.score > other.score) {
            return 1;
        } else if (this.score < other.score) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Similar)) return false;

        Similar similar = (Similar) o;

        return authorId.equals(similar.authorId);

    }
}
