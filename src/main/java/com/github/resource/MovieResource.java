package com.github.resource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.model.Movie;
import com.github.producer.MovieProducer;
import com.github.service.MovieService;

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
    public Response send(Movie movie) throws JsonProcessingException {
        producer.sendMovieToKafka(movie);
        // Return an 202 - Accepted response.
        return Response.accepted().build();
    }
}
