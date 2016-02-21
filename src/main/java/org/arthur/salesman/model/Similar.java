package org.arthur.salesman.model;

/**
 * A score given to its similar author
 *
 * @author Arthur Grava (arthur at luizalabs.com).
 */
public class Similar {

    private String authorId;
    private Double score;

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
}
