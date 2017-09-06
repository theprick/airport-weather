package com.crossover.trial.weather.endpoint;

import com.crossover.trial.weather.data.FrequencyDataStore;
import com.crossover.trial.weather.data.InformationDataStore;
import com.crossover.trial.weather.interceptor.FrequencyUpdater;
import com.crossover.trial.weather.model.AirportData;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.validation.generic.Error;
import com.crossover.trial.weather.validation.generic.GenericInputRequestValidator;
import com.crossover.trial.weather.validation.generic.InputValidationException;
import com.google.gson.Gson;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.logging.Logger;

/**
 * The Weather App REST endpoint allows clients to query, update and check health stats. Currently, all data is
 * held in memory. The end point deploys to a single container
 *
 * @author code test administrator
 */
@Path("/query")
public class RestWeatherQueryEndpoint implements WeatherQueryEndpoint {

    public final static Logger LOGGER = Logger.getLogger("WeatherQuery");

    /** earth radius in KM */
    private static final double R = 6372.8;

    /** shared gson json to object factory */
    private static final Gson gson = new Gson();

    private static InformationDataStore informationDataStore = InformationDataStore.getInstance();

    private static FrequencyDataStore frequencyDataStore = FrequencyDataStore.getInstance();

    /**
     * Retrieve service health including total size of valid data points and request frequency information.
     *
     * @return health stats for the service as a string
     */
    @Override
    @GET
    @Path("/ping")
    public String ping() {
        Map<String, Object> retval = new HashMap<>();

        int datasize = 0;
        for (AtmosphericInformation ai : informationDataStore.listAtmosphericInformation()) {
            // we only count recent readings
            if (ai.getCloudCover() != null
                    || ai.getHumidity() != null
                    || ai.getPressure() != null
                    || ai.getPrecipitation() != null
                    || ai.getTemperature() != null
                    || ai.getWind() != null) {
                // updated in the last day
                if (ai.getLastUpdateTime() > System.currentTimeMillis() - 86400000) {
                    datasize++;
                }
            }
        }
        retval.put("datasize", datasize);

        Map<String, Double> freq = new HashMap<>();
        // fraction of queries
        for (AirportData data : informationDataStore.listAirports()) {
            int totalRequests = frequencyDataStore.totalNumberOfRequests();
            double frac = 0;
            if(totalRequests > 0) {
                frac = (double) frequencyDataStore.getRequestFrequency(data.getIata()) / totalRequests;
            }
            freq.put(data.getIata(), frac);
        }
        retval.put("iata_freq", freq);

        int m = frequencyDataStore.listRadii().stream()
                .max(Double::compare)
                .orElse(1000.0).intValue() + 1;

        int[] hist = new int[m];

        for(Double radius : frequencyDataStore.listRadii()) {
            int i = radius.intValue() / 10;
            hist[i] += frequencyDataStore.getRadiusFrequency(radius);
        }

        retval.put("radius_freq", hist);

        return gson.toJson(retval);
    }

    /**
     * Given a query in json format {'iata': CODE, 'radius': km} extracts the requested airport information and
     * return a list of matching atmosphere information.
     *
     * @param iataCode the iataCode
     * @param radiusString the radius in km
     *
     * @return a list of atmospheric information
     */
    @Override
    @GET
    @Path("/weather/{iata}/{radius}")
    @Produces(MediaType.APPLICATION_JSON)
    @FrequencyUpdater
    public Response weather(@PathParam("iata") String iataCode, @PathParam("radius") String radiusString) {
        try {
            new GenericInputRequestValidator().validate(
                    Arrays.asList("iata", "radius"),
                    Arrays.asList(iataCode, radiusString));
        } catch (InputValidationException ex) {
            GenericEntity<List<Error>> errors = new GenericEntity<List<Error>>(ex.getErrors()){};
            return Response.status(Response.Status.BAD_REQUEST).entity(errors).build();
        }

        double radius = radiusString == null || radiusString.trim().isEmpty() ? 0 : Double.valueOf(radiusString);

        List<AtmosphericInformation> retval = new ArrayList<>();
        if (radius == 0) {
            //if iata exists and radius == 0 return information coresponding for that airport
            AirportData airportData = informationDataStore.findAirportData(iataCode);
            if(airportData != null) {
                retval.add(informationDataStore.findAtmosphericInformation(airportData));
            } else {
                retval.add(new AtmosphericInformation.Builder().build());
            }
        } else {
            AirportData ref = informationDataStore.findAirportData(iataCode);
            for(AirportData airportData : informationDataStore.listAirports()) {
                if (calculateDistance(ref, airportData) <= radius){
                    AtmosphericInformation ai = informationDataStore.findAtmosphericInformation(airportData);
                    if(!ai.isEmpty()) {
                        retval.add(ai);
                    }
                }
            }
        }
        return Response.status(Response.Status.OK).entity(retval).build();
    }

    /**
     * Haversine distance between two airports.
     *
     * @param ad1 airport 1
     * @param ad2 airport 2
     * @return the distance in KM
     */
    private double calculateDistance(AirportData ad1, AirportData ad2) {
        double deltaLat = Math.toRadians(ad2.getLatitude() - ad1.getLatitude());
        double deltaLon = Math.toRadians(ad2.getLongitude() - ad1.getLongitude());
        double a =  Math.pow(Math.sin(deltaLat / 2), 2) + Math.pow(Math.sin(deltaLon / 2), 2)
                * Math.cos(ad1.getLatitude()) * Math.cos(ad2.getLatitude());
        double c = 2 * Math.asin(Math.sqrt(a));
        return R * c;
    }
}
