package com.github.model;

import org.bson.Document;

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

    @Override
    public String toString() {
        return "Movie{" +
                "title='" + title + '\'' +
                ", year=" + year +
                ", description='" + description + '\'' +
                '}';
    }

    public Document toDocument() {
        Document docMovie = new Document();
        docMovie.append("title", this.title);
        docMovie.append("year", this.year);
        docMovie.append("description", this.description);

        return docMovie;
    }
}
