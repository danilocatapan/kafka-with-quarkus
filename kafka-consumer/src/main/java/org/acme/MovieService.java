package org.acme;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import org.acme.mongodb.Movie;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.bson.Document;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class MovieService {

    private final Logger logger = Logger.getLogger(MovieService.class);

    @ConfigProperty(name = "quarkus.mongodb.database")
    String database;

    String collectionMovies = "movies";

    @Inject
    MongoClient mongoClient;

    @PostConstruct
    public void init() {
        logger.infof("[INFRA] STARTING DATABASE: "+ database +" COLLECTION: "+collectionMovies );
    }

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

    public void add(ConsumerRecord<Integer, String> movie){
        Document document = new Document()
                .append("title", movie.value())
                .append("year", movie.key());

        getCollection().insertOne(document);
    }

    private MongoCollection getCollection(){
        return mongoClient.getDatabase("kafka-consumer").getCollection("movies");
    }
}
