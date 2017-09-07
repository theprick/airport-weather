package com.crossover.trial.weather.endpoint;

import com.crossover.trial.weather.data.FrequencyDataStore;
import com.crossover.trial.weather.data.InformationDataStore;
import com.crossover.trial.weather.model.*;
import com.crossover.trial.weather.validation.DataPointWithType;
import com.crossover.trial.weather.validation.generic.Error;
import com.crossover.trial.weather.validation.generic.GenericInputRequestValidator;
import com.crossover.trial.weather.validation.generic.InputValidationException;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * A REST implementation of the WeatherCollector API. Accessible only to airport weather collection
 * sites via secure VPN.
 *
 * @author code test administrator
 */

@Path("/collect")
public class RestWeatherCollectorEndpoint implements WeatherCollectorEndpoint {

    public final static Logger LOGGER = Logger.getLogger(RestWeatherCollectorEndpoint.class.getName());

    //shared gson json to object factory
    private final static Gson gson = new Gson();

    private static InformationDataStore informationDataStore
            = InformationDataStore.getInstance();

    private static FrequencyDataStore frequencyDataStore =
            FrequencyDataStore.getInstance();

    @Override
    @GET
    @Path("/ping")
    public Response ping() {
        return Response.status(Response.Status.OK).entity("ready").build();
    }

    @Override
    @POST
    @Consumes("application/json")
    @Produces("application/json")
    @Path("/weather/{iata}/{pointType}")
    public Response updateWeather(@PathParam("iata") String iataCode,
                                  @PathParam("pointType") String pointType,
                                  String datapointJson) {
        if(iataCode != null) {
            iataCode = iataCode.toUpperCase();
        }
        try {
            new GenericInputRequestValidator().validate(
                    Arrays.asList("iata", "dataPoint"),
                    Arrays.asList(iataCode, new DataPointWithType(pointType, datapointJson)));
        } catch (InputValidationException ex) {
            GenericEntity<List<Error>> errors = new GenericEntity<List<Error>>(ex.getErrors()){};
            return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
        }
        AirportData airportData = informationDataStore.findAirportData(iataCode);
        if (airportData != null) {
            addDataPoint(airportData, pointType, gson.fromJson(datapointJson, DataPoint.class));
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.status(Response.Status.OK).build();
    }

    @Override
    @GET
    @Path("/airports")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAirports() {
        Set<String> airports = informationDataStore.listAirportIataCodes();
        return Response.status(Response.Status.OK).entity(airports).build();
    }

    @Override
    @GET
    @Path("/airport/{iata}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAirport(@PathParam("iata") String iataCode) {
        if(iataCode != null) {
            iataCode = iataCode.toUpperCase();
        }
        try {
            new GenericInputRequestValidator().validate(
                    Collections.singletonList("iata"),
                    Collections.singletonList(iataCode));
        } catch (InputValidationException ex) {
            GenericEntity<List<Error>> errors = new GenericEntity<List<Error>>(ex.getErrors()){};
            return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
        }
        AirportData ad = informationDataStore.findAirportData(iataCode);
        if(ad != null) {
            return Response.status(Response.Status.OK).entity(ad).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
    }

    @Produces("application/json")
    @Override
    @POST
    @Path("/airport/{iata}/{lat}/{long}")
    public Response addAirport(@PathParam("iata") String iataCode,
                               @PathParam("lat") String latString,
                               @PathParam("long") String longString) {
        if(iataCode != null) {
            iataCode = iataCode.toUpperCase();
        }
        try {
            new GenericInputRequestValidator().validate(
                    Arrays.asList("iata", "lat", "long"),
                    Arrays.asList(iataCode, latString, longString));
        } catch (InputValidationException ex) {
            GenericEntity<List<Error>> errors = new GenericEntity<List<Error>>(ex.getErrors()){};
            return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
        }
        AirportData airportData = new AirportData.Builder().withIata(iataCode)
                .withLatitude(Double.valueOf(latString))
                .withLongitude(Double.valueOf(longString)).build();
        informationDataStore.addAirport(airportData);
        return Response.status(Response.Status.OK).build();
    }

    @Produces("application/json")
    @Override
    @DELETE
    @Path("/airport/{iata}")
    public Response deleteAirport(@PathParam("iata") String iataCode) {
        if(iataCode != null) {
            iataCode = iataCode.toUpperCase();
        }
        try {
            new GenericInputRequestValidator().validate(
                    Collections.singletonList("iata"),
                    Collections.singletonList(iataCode));
        } catch (InputValidationException ex) {
            GenericEntity<List<Error>> errors = new GenericEntity<List<Error>>(ex.getErrors()){};
            return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
        }
        informationDataStore.deleteAirport(iataCode);
        frequencyDataStore.deleteAirport(iataCode);
        return Response.status(Response.Status.OK).build();
    }

    @Override
    @GET
    @Path("/exit")
    public Response exit() {
        System.exit(0);
        return Response.noContent().build();
    }

    //
    // Internal support methods
    //

    /**
     * Update the airports weather data with the collected data.
     *
     * @param airportData airport data
     * @param pointType   the point type {@link DataPointType}
     * @param dataPoint   a datapoint object holding pointType data
     */
    private void addDataPoint(AirportData airportData, String pointType, DataPoint dataPoint) {
        AtmosphericInformation newInfo =
                new AtmosphericInformationFactory().getAtmosphericInformation(DataPointType.valueOf(pointType.toUpperCase()), dataPoint);
        informationDataStore.updateDataPoint(airportData, newInfo);
    }
}


