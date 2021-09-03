package org.acme.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.acme.mongodb.Movie;
import org.acme.service.MovieService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.reactive.messaging.Acknowledgment;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;

@ApplicationScoped
public class MovieConsumer {

    private final Logger logger = Logger.getLogger(MovieConsumer.class);

    @Inject @Channel("event-dlq")
    Emitter<String> failEmitter;

    @Inject
    MovieService movieService;

    @Incoming("movies-in")
    @Retry(delay = 10, maxRetries = 5)
    @Acknowledgment(Acknowledgment.Strategy.POST_PROCESSING)
    public void receive(ConsumerRecord<String, String> event) throws JsonProcessingException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Movie movie = mapper.readValue(event.value(), Movie.class);

            logger.infof("Event: %s", event.value());
            logger.infof("Movie: %s", movie.toString());

            movieService.add(movie);
        } catch (Exception e) {
            logger.error("[ERROR AT] TYPE: INFRA | OFFSET: "+event.offset()+" | PARTITION: "+event.partition()+" | EVENTO: "+event.value());
            logger.error("[CAUSE] "+e.getMessage());

            CompletionStage<Void> completionStage = this.failEmitter(event, e, "", "movies");

            completionStage.whenComplete((acked, nacked) -> {
                if (nacked != null) {
                    logger.error("[ERROR AT] TYPE: DLQ | MSG: ERROR SENDING DLQ MESSAGE");
                    logger.error("[ERROR AT] OFFSET: "+event.offset()+" | PARTITION: "+event.partition()+" | EVENTO: "+event.value());
                    logger.error("[CAUSE] " + nacked.getMessage());
                    nacked.printStackTrace();
                } else {
                    logger.error("[ERROR AT] MSG: SUCCESS SENDING TO DLQ | OFFSET: "+event.offset()+" | PARTITION: "+event.partition()+" | EVENTO: "+event.value());
                }
            });

            return;
        }
    }

    private CompletionStage<Void> failEmitter(ConsumerRecord<String, String> event, Exception cause, String additionalInfo, String eventSource) throws JsonProcessingException {

        Map<String, Object> failMessage = new HashMap<String, Object>();
        failMessage.put("timestamp", event.timestamp());
        failMessage.put("topic", event.topic());
        failMessage.put("offset", event.offset());
        failMessage.put("partition", event.partition());
        failMessage.put("payload", event.value());
        failMessage.put("exception.type", cause.getClass().getName());
        failMessage.put("exception.msg", cause.getLocalizedMessage());
        failMessage.put("additionalInfo", additionalInfo);
        failMessage.put("event.source", eventSource);
        failMessage.put("event.type", "DLQ");
        Header[] headers = event.headers().toArray();
        if (headers != null) {
            for (Header header : headers) {
                failMessage.put("header."+header.key(), header.value());
            }
        }

        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(failMessage);

        return failEmitter.send(jsonResult);
    }
}
