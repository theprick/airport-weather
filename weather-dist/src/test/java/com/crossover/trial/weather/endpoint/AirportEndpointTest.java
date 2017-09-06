package com.crossover.trial.weather.endpoint;

import com.crossover.trial.weather.TestUtils;
import com.crossover.trial.weather.model.AirportData;
import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.validation.generic.Error;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.*;

import static com.crossover.trial.weather.validation.generic.ErrorCode.INVALID_VALUE;
import static com.crossover.trial.weather.validation.generic.ErrorCode.MISSING_PARAMETER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by Popescu Adrian-Dumitru on 06.09.2017.
 *
 * Tests
 */
public class AirportEndpointTest {

    private WeatherCollectorEndpoint _update = new RestWeatherCollectorEndpoint();

    @Before
    public void setUp() throws Exception {
        TestUtils.init();
    }

    @Test
    public void testGetAirports() {
        Response response = _update.getAirports();
        assertEquals(200, response.getStatus());
        Set<String> airports = (Set<String>) response.getEntity();
        assertEquals(new HashSet<>(Arrays.asList("BOS", "EWR", "JFK", "LGA", "MMU")), airports);
    }

    @Test
    public void testGetAirport() {
        Response response = _update.getAirport("BOS");
        assertEquals(200, response.getStatus());
        AirportData airport = (AirportData) response.getEntity();
        assertEquals(new AirportData.Builder().withIata("BOS").withLatitude(42.364347).withLongitude(-71.005181).build(),
                airport);
    }

    @Test
    public void testGetAirportNull() {
        Response response = _update.getAirport(null);
        assertEquals(400, response.getStatus());
        List<Error> errors = (List<Error>)response.getEntity();
        assertEquals(
                Collections.singletonList(
                        new Error(MISSING_PARAMETER, "iata", "The parameter is mandatory")
                ),
                errors);
    }

    @Test
    public void testGetAirportInvalid() {
        Response response = _update.getAirport("invalid");
        assertEquals(400, response.getStatus());
        List<Error> errors = (List<Error>)response.getEntity();
        assertEquals(
                Collections.singletonList(
                        new Error(INVALID_VALUE, "iata", "The iata code is formed of 3 upper case letters")
                ),
                errors);
    }

    @Test
    public void testDeleteAirport() {
        assertList(new HashSet<>(Arrays.asList("BOS", "EWR", "JFK", "LGA", "MMU")));

        Response response = _update.deleteAirport("BOS");
        assertEquals(200, response.getStatus());

        assertNullItem("BOS");
        assertList(new HashSet<>(Arrays.asList("EWR", "JFK", "LGA", "MMU")));
    }

    @Test
    public void testDeleteAirportNull() {
        Response response = _update.deleteAirport(null);
        assertEquals(400, response.getStatus());
        List<Error> errors = (List<Error>)response.getEntity();
        assertEquals(
                Collections.singletonList(
                        new Error(MISSING_PARAMETER, "iata", "The parameter is mandatory")
                ),
                errors);
    }

    @Test
    public void testDeleteAirportInvalid() {
        Response response = _update.deleteAirport("invalid");
        assertEquals(400, response.getStatus());
        List<Error> errors = (List<Error>)response.getEntity();
        assertEquals(
                Collections.singletonList(
                        new Error(INVALID_VALUE, "iata", "The iata code is formed of 3 upper case letters")
                ),
                errors);
    }

    @Test
    public void testAddAirport() {
        assertList(new HashSet<>(Arrays.asList("BOS", "EWR", "JFK", "LGA", "MMU")));

        Response response = _update.addAirport("ABC", "47.899798", "-61.9898989");
        assertEquals(200, response.getStatus());

        assertItem("ABC", 47.899798, -61.9898989);
        assertList(new HashSet<>(Arrays.asList("BOS", "EWR", "JFK", "LGA", "MMU", "ABC")));
    }

    @Test
    public void testAddAirportInvalid() {
        Response response = _update.addAirport("invalid", "invalid", "invalid");
        assertEquals(400, response.getStatus());
        List<Error> errors = (List<Error>)response.getEntity();
        assertEquals(
                Arrays.asList(
                        new Error(INVALID_VALUE, "iata", "The iata code is formed of 3 upper case letters"),
                        new Error(INVALID_VALUE, "lat","The parameter must be a valid decimal number"),
                        new Error(INVALID_VALUE, "long","The parameter must be a valid decimal number")
                ),
                errors);
    }

    @Test
    public void testAddAirportNull() {
        Response response = _update.addAirport(null, null, null);
        assertEquals(400, response.getStatus());
        List<Error> errors = (List<Error>)response.getEntity();
        assertEquals(
                Arrays.asList(
                        new Error(MISSING_PARAMETER, "iata", "The parameter is mandatory"),
                        new Error(MISSING_PARAMETER, "lat","The parameter is mandatory"),
                        new Error(MISSING_PARAMETER, "long","The parameter is mandatory")
                ),
                errors);
    }

    private void assertList(Set<String> expected) {
        Response response = _update.getAirports();
        assertEquals(200, response.getStatus());
        Set<String> airports = (Set<String>) response.getEntity();
        assertEquals(expected, airports);
    }

    private void assertItem(String iata, double latitude, double longitude) {
        Response response = _update.getAirport(iata);
        assertEquals(200, response.getStatus());
        AirportData airportData = (AirportData) response.getEntity();
        assertEquals(
                new AirportData.Builder().withIata(iata).withLatitude(latitude).withLongitude(longitude).build(),
                airportData);
    }

    private void assertNullItem(String iata) {
        Response response = _update.getAirport(iata);
        assertEquals(200, response.getStatus());
        assertNull(response.getEntity());
    }
}
