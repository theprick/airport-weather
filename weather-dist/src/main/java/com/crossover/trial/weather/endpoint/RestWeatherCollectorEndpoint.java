package com.crossover.trial.weather.endpoint;

import com.crossover.trial.weather.data.AirportsDataStore;
import com.crossover.trial.weather.model.*;
import com.google.gson.Gson;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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

    private static AirportsDataStore airportsDataStore
            = AirportsDataStore.getInstance();

    @Override
    @GET
    @Path("/ping")
    public Response ping() {
        return Response.status(Response.Status.OK).entity("ready").build();
    }

    @Override
    @POST
    @Path("/weather/{iata}/{pointType}")
    public Response updateWeather(@PathParam("iata") String iataCode,
                                  @PathParam("pointType") String pointType,
                                  String datapointJson) {
        try {
            AirportData airportData = airportsDataStore.findAirportData(iataCode);
            if(airportData != null) {
                addDataPoint(airportData, pointType, gson.fromJson(datapointJson, DataPoint.class));
            }
        } catch (WeatherException e) {
            e.printStackTrace();
        }
        return Response.status(Response.Status.OK).build();
    }

    @Override
    @GET
    @Path("/airports")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAirports() {
        Set<String> airports = airportsDataStore.listAirportIataCodes();
        return Response.status(Response.Status.OK).entity(airports).build();
    }

    @Override
    @GET
    @Path("/airport/{iata}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAirport(@PathParam("iata") String iata) {
        AirportData ad = airportsDataStore.findAirportData(iata);
        return Response.status(Response.Status.OK).entity(ad).build();
    }

    @Override
    @POST
    @Path("/airport/{iata}/{lat}/{long}")
    public Response addAirport(@PathParam("iata") String iata,
                               @PathParam("lat") String latString,
                               @PathParam("long") String longString) {
        AirportData airportData = new AirportData.Builder().withIata(iata)
                .withLatitude(Double.valueOf(latString))
                .withLongitude(Double.valueOf(longString)).build();
        airportsDataStore.addAirport(airportData);
        return Response.status(Response.Status.OK).build();
    }

    @Override
    @DELETE
    @Path("/airport/{iata}")
    public Response deleteAirport(@PathParam("iata") String iata) {
        airportsDataStore.deleteAirport(iata);
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
     * @param airportData  airport data
     * @param pointType the point type {@link DataPointType}
     * @param dataPoint a datapoint object holding pointType data
     * @throws WeatherException if the update can not be completed
     */
    private void addDataPoint(AirportData airportData, String pointType, DataPoint dataPoint) throws WeatherException {
        AtmosphericInformation newInfo =
                new AtmosphericInformationFactory().getAtmosphericInformation(DataPointType.valueOf(pointType.toUpperCase()), dataPoint);
        airportsDataStore.updateDataPoint(airportData, newInfo);
    }
}


