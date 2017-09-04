package com.crossover.trial.weather.interceptor;

import com.crossover.trial.weather.data.RequestFrequencyDataStore;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * Created by Popescu Adrian-Dumitru on 03.09.2017.
 * Filter used to record information about how often requests are made.
 */
@Provider
@FrequencyUpdater
public class WeatherQueryFilter implements ContainerResponseFilter {

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        MultivaluedMap<String, String> pathParameters = requestContext.getUriInfo().getPathParameters();
        String iata = pathParameters.getFirst("iata");
        Double radius = Double.valueOf(pathParameters.getFirst("radius"));

        RequestFrequencyDataStore frequencyDataStore = RequestFrequencyDataStore.getInstance();
        frequencyDataStore.updateRequestFrequency(iata);
        frequencyDataStore.updateRadiusFrequency(radius);
    }
}
