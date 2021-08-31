package org.acme.mongodb;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "movies")
public class Movie extends PanacheMongoEntity {

    public Movie() {
    }

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
