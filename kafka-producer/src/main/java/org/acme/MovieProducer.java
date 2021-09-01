package org.acme;

import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class MovieProducer {

    private final org.jboss.logging.Logger logger = Logger.getLogger(MovieProducer.class);

    @Inject @Channel("movies-out")
    Emitter<Record<Integer, String>> emitter;

    public void sendMovieToKafka(Movie movie) {
        CompletionStage<Void> ack = emitter.send(Record.of(movie.getYear(), movie.getTitle()));

        ack.whenComplete((acked, nacked) -> {
            if (nacked != null) {
                logger.infof("[ERROR] " + nacked.getMessage());
            } else {
                logger.infof("[SUCCESS] SENDING TO MOVIES-IN");
            }
        });
    }
}