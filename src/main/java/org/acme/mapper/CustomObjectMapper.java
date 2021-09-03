package org.acme.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.jackson.ObjectMapperCustomizer;

import javax.enterprise.inject.Instance;
import javax.inject.Singleton;

public class CustomObjectMapper {

    @Singleton
    ObjectMapper objectMapper(Instance<ObjectMapperCustomizer> customizers) {
        ObjectMapper mapper = new ObjectMapper();

        for (ObjectMapperCustomizer customizer : customizers) {
            customizer.customize(mapper);
        }

        return mapper;
    }
}
