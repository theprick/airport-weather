package com.crossover.trial.weather.endpoint;

import com.crossover.trial.weather.data.AtmosphericInformationDataStore;
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

    private static AtmosphericInformationDataStore atmosphericInformationDataStore
            = AtmosphericInformationDataStore.getInstance();

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
            addDataPoint(iataCode, pointType, gson.fromJson(datapointJson, DataPoint.class));
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
        Set<String> airports = atmosphericInformationDataStore.listAirportIataCodes();
        return Response.status(Response.Status.OK).entity(airports).build();
    }

    @Override
    @GET
    @Path("/airport/{iata}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAirport(@PathParam("iata") String iata) {
        AirportData ad = atmosphericInformationDataStore.findAirportData(iata);
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
        atmosphericInformationDataStore.addAirport(airportData);
        return Response.status(Response.Status.CREATED).build();
    }

    @Override
    @DELETE
    @Path("/airport/{iata}")
    public Response deleteAirport(@PathParam("iata") String iata) {
        return Response.status(Response.Status.NOT_IMPLEMENTED).build();
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
     * @param iataCode  the 3 letter IATA code
     * @param pointType the point type {@link DataPointType}
     * @param dataPoint a datapoint object holding pointType data
     * @throws WeatherException if the update can not be completed
     */

    private void addDataPoint(String iataCode, String pointType, DataPoint dataPoint) throws WeatherException {
        AtmosphericInformation ai = atmosphericInformationDataStore.findAtmosphericInformation(iataCode);
        updateAtmosphericInformation(ai, pointType, dataPoint);
        atmosphericInformationDataStore.addAtmosphericInformation(iataCode, ai);
    }

    /**
     * update atmospheric information with the given data point for the given point type
     *
     * @param ai        the atmospheric information object to update
     * @param pointType the data point type as a string
     * @param dp        the actual data point
     */
    private void updateAtmosphericInformation(AtmosphericInformation ai, String pointType, DataPoint dp) throws WeatherException {
        final DataPointType dataPointType = DataPointType.valueOf(pointType.toUpperCase());

        if (dataPointType.equals(DataPointType.WIND)) {
            if (dp.getMean() >= 0) {
                ai.setWind(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        if (dataPointType.equals(DataPointType.TEMPERATURE)) {
            if (dp.getMean() >= -50 && dp.getMean() < 100) {
                ai.setTemperature(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        if (dataPointType.equals(DataPointType.HUMIDTY)) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setHumidity(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        if (dataPointType.equals(DataPointType.PRESSURE)) {
            if (dp.getMean() >= 650 && dp.getMean() < 800) {
                ai.setPressure(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        if (dataPointType.equals(DataPointType.CLOUDCOVER)) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setCloudCover(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        if (dataPointType.equals(DataPointType.PRECIPITATION)) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setPrecipitation(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }
        throw new IllegalStateException("couldn't update atmospheric data");
    }
}

