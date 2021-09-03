package org.acme.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.acme.model.Movie;
import org.acme.producer.MovieProducer;
import org.acme.service.MovieService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MovieResource {

    @Inject
    MovieProducer producer;

    @Inject
    MovieService movieService;

    @GET
    public List<Movie> list() {
        return movieService.list();
    }

    @POST
    @Path("/streams")
    public Response send(Movie movie) throws JsonProcessingException {
        producer.sendMovieToKafka(movie);
        // Return an 202 - Accepted response.
        return Response.accepted().build();
    }
}
