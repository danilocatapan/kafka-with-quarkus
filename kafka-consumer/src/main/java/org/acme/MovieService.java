package org.acme;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import io.smallrye.reactive.messaging.kafka.Record;
import org.acme.mongodb.Movie;
import org.bson.Document;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class MovieService {

    @Inject
    MongoClient mongoClient;

    public List<Movie> list(){
        List<Movie> list = new ArrayList<>();

        try (MongoCursor<Document> cursor = getCollection().find().iterator()) {
            while (cursor.hasNext()) {
                Document document = cursor.next();
                Movie movie = new Movie();
                movie.setTitle(document.getString("title"));
                movie.setYear(document.getInteger("year"));
                list.add(movie);
            }
        }
        return list;
    }

    public void add(Record<Integer, String> movie){
        Document document = new Document()
                .append("title", movie.value())
                .append("year", movie.key());

        getCollection().insertOne(document);
    }

    private MongoCollection getCollection(){
        return mongoClient.getDatabase("kafka-consumer").getCollection("movies");
    }
}
