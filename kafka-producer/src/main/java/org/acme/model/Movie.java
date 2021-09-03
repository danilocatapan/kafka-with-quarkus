package org.acme.model;

public class Movie {

    public Movie() {
    }

    public Movie(String title, int year, String description) {
        this.title = title;
        this.year = year;
        this.description = description;
    }

    String title;
    int year;
    String description;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
