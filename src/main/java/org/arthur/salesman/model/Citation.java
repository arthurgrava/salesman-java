package org.arthur.salesman.model;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Arthur Grava (arthur at luizalabs.com).
 */
public class Citation {

    private String authorId;
    private String articleId;
    private Double score;

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public boolean equals(Citation obj) {
        if (StringUtils.isNotBlank(this.articleId) && this.articleId.equals(obj.articleId)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return equals((Citation) obj);
    }

    @Override
    public String toString() {
        return "[" + this.authorId + ", " + this.articleId + ", " + this.score + "]";
    }
}
