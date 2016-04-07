package org.arthur.salesman.model;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.04.07
 */
public class Used {

    private Recommendation recommendation;
    private int useful;

    public Used(Recommendation recommendation, int useful) {
        this.recommendation = recommendation;
        this.useful = useful;
    }

    public Used() { }

    public Recommendation getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(Recommendation recommendation) {
        this.recommendation = recommendation;
    }

    public int getUseful() {
        return useful;
    }

    public void setUseful(int useful) {
        this.useful = useful;
    }
}
