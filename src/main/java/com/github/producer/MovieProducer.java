package com.github.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.model.Movie;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class MovieProducer {

    private final Logger logger = Logger.getLogger(MovieProducer.class);

    @Inject @Channel("movies-out")
    Emitter<String> emitter;

    public void sendMovieToKafka(Movie movie) throws JsonProcessingException {
        Map<String, Object> failMessage = new HashMap<String, Object>();
        failMessage.put("title", movie.getTitle());
        failMessage.put("year", movie.getYear());
        failMessage.put("description", movie.getDescription());

        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(failMessage);

        CompletionStage<Void> ack = emitter.send(jsonResult);

        ack.whenComplete((acked, nacked) -> {
            if (nacked != null) {
                logger.infof("[ERROR] " + nacked.getMessage());
            } else {
                logger.infof("[SUCCESS] SENDING TO MOVIES-IN");
            }
        });
    }
}