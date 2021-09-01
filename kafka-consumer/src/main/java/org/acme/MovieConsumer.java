package org.acme;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    public void receive(ConsumerRecord<Integer, String> movie) throws JsonProcessingException {
        try {
            logger.infof("Got a movie: %d - %s", movie.key(), movie.value());
            movieService.add(movie);
        } catch (Exception e) {
            logger.error("[ERROR AT] TYPE: ADDING | OFFSET: "+movie.offset()+" | PARTITION: "+movie.partition()+" | EVENTO: "+movie.value());
            logger.error("[CAUSE] "+e.getMessage());

            CompletionStage<Void> completionStage = this.failEmitter(movie, e, "", "vinculo");

            completionStage.whenComplete((acked, nacked) -> {
                if (nacked != null) {
                    logger.error("[ERROR AT] TYPE: DLQ | MSG: ERROR SENDING DLQ MESSAGE");
                    logger.error("[ERROR AT] OFFSET: "+movie.offset()+" | PARTITION: "+movie.partition()+" | EVENTO: "+movie.value());
                    logger.error("[CAUSE] " + nacked.getMessage());
                    nacked.printStackTrace();
                } else {
                    logger.error("[ERROR AT] MSG: SUCCESS SENDING TO DLQ | OFFSET: "+movie.offset()+" | PARTITION: "+movie.partition()+" | EVENTO: "+movie.value());
                }
            });

            return;
        }
    }

    private CompletionStage<Void> failEmitter(ConsumerRecord<Integer, String> event, Exception cause, String additionalInfo, String eventSource) throws JsonProcessingException {

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
