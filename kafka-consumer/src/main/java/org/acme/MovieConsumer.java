package org.acme;

import io.smallrye.reactive.messaging.kafka.Record;
import org.acme.mongodb.Movie;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class MovieConsumer {

    private final Logger logger = Logger.getLogger(MovieConsumer.class);

    @Inject
    MovieService movieService;

    @Incoming("movies-in")
    @Retry(delay = 10, maxRetries = 5)
    public void receive(Record<Integer, String> movie) {
        logger.infof("Got a movie: %d - %s x", movie.key(), movie.value());

        Movie newMovie = new Movie();
        newMovie.setTitle(movie.value());
        newMovie.setYear(movie.key());
        movieService.add(newMovie);
    }
}
