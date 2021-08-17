package org.acme;

public class Movie {

    public Movie(String title, int year) {
        this.title = title;
        this.year = year;
    }

    String title;
    int year;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}
