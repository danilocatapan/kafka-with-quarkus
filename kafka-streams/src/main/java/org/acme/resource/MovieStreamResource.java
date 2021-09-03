package org.acme.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.acme.model.Movie;
import org.acme.producer.MovieProducer;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/streams")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MovieStreamResource {

    @Inject
    MovieProducer producer;

    @POST
    public Response send(Movie movie) throws JsonProcessingException {
        producer.sendMovieToKafka(movie);
        // Return an 202 - Accepted response.
        return Response.accepted().build();
    }
}
