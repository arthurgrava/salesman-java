package org.arthur.salesman.model;

/**
 * @author Arthur Grava (arthur.grava at gmail.com) - 2016.04.04
 */
public class Coauthorship implements Comparable<Coauthorship> {

    private String authorId;
    private int year;

    public Coauthorship(String authorId, int year) {
        this.authorId = authorId;
        this.year = year;
    }

    public Coauthorship() { }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    @Override
    public int compareTo(Coauthorship o) {
        return this.year < o.year ? -1 : this.year == o.year ? 0 : 1;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Coauthorship)) {
            return false;
        }

        Coauthorship other = (Coauthorship) o;
        return this.authorId.equals(other.getAuthorId()) && this.year == other.year;
    }

    @Override
    public int hashCode() {
        int result = authorId.hashCode();
        result = 31 * result + year;
        return result;
    }

    @Override
    public String toString() {
        return "Coauthorship{authorId='" + authorId + "', year=" + year + "}";
    }
}
