package com.crossover.trial.weather.endpoint;

import com.crossover.trial.weather.TestUtils;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.model.DataPointType;
import com.crossover.trial.weather.validation.generic.Error;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.*;

import static com.crossover.trial.weather.validation.generic.ErrorCode.INVALID_VALUE;
import static com.crossover.trial.weather.validation.generic.ErrorCode.MISSING_PARAMETER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WeatherEndpointTest {

    private WeatherQueryEndpoint _query = new RestWeatherQueryEndpoint();

    private WeatherCollectorEndpoint _update = new RestWeatherCollectorEndpoint();

    private Gson _gson = new Gson();

    private DataPoint _dp;

    @Before
    public void setUp() throws Exception {
        TestUtils.init();
        _dp = new DataPoint.Builder()
                .withCount(10).withFirst(10).withMedian(20).withLast(30).withMean(22).build();
        _update.updateWeather("BOS", "wind", _gson.toJson(_dp));
        _query.weather("BOS", "0").getEntity();
    }

    @Test
    public void testPing() throws Exception {
        String ping = _query.ping();
        JsonElement pingResult = new JsonParser().parse(ping);
        assertEquals(1, pingResult.getAsJsonObject().get("datasize").getAsInt());
        assertEquals(5, pingResult.getAsJsonObject().get("iata_freq").getAsJsonObject().entrySet().size());
    }

    @Test
    public void testGet() throws Exception {
        List<AtmosphericInformation> ais = (List<AtmosphericInformation>) _query.weather("BOS", "0").getEntity();
        assertEquals(ais.get(0).getWind(), _dp);
    }

    @Test
    public void testGetInvalid() {
        Response response = _query.weather("invalid", "-1");
        List<Error> errors = (List<Error>)response.getEntity();
        assertEquals(400, response.getStatus());
        assertEquals(
                Arrays.asList(
                        new Error(INVALID_VALUE, "iata", "The iata code is formed of 3 upper case letters"),
                        new Error(INVALID_VALUE, "radius","The parameter must be a valid decimal number greater or equal to 0")
                ),
                errors);
    }

    @Test
    public void testGetNull() {
        Response response = _query.weather(null, null);
        List<Error> errors = (List<Error>)response.getEntity();
        assertEquals(400, response.getStatus());
        assertEquals(
                Collections.singletonList(
                        new Error(MISSING_PARAMETER, "iata", "The parameter is mandatory")
                ),
                errors);
    }

    @Test
    public void testGetNearby() {
        // check datasize response
        _update.updateWeather("JFK", "wind", _gson.toJson(_dp));
        _dp.setMean(40);
        _update.updateWeather("EWR", "wind", _gson.toJson(_dp));
        _dp.setMean(30);
        _update.updateWeather("LGA", "wind", _gson.toJson(_dp));

        List<AtmosphericInformation> ais = (List<AtmosphericInformation>) _query.weather("JFK", "200").getEntity();
        assertEquals(3, ais.size());
        // added more assertions
        checkAtmosphericInformation(new DataPoint.Builder()
                .withCount(10).withFirst(10).withMedian(20).withLast(30).withMean(22).build(), ais);
        checkAtmosphericInformation(new DataPoint.Builder()
                .withCount(10).withFirst(10).withMedian(20).withLast(30).withMean(40).build(), ais);
        checkAtmosphericInformation(new DataPoint.Builder()
                .withCount(10).withFirst(10).withMedian(20).withLast(30).withMean(30).build(), ais);
    }

    private void checkAtmosphericInformation(DataPoint dataPoint, List<AtmosphericInformation> atmosphericInformationList) {
        Assert.assertTrue(atmosphericInformationList.contains(
                new AtmosphericInformation.Builder().withWind(dataPoint).build()));
    }

    @Test
    public void testGetWeatherNonExistingAirport() {
        HashSet<String> airports = new HashSet<>(Arrays.asList("BOS", "EWR", "JFK", "LGA", "MMU"));
        assertList(airports);
        String nonExistentCode = "XYZ";
        assertFalse(airports.contains(nonExistentCode));

        Response response = _query.weather(nonExistentCode, "0");
        assertEquals(404, response.getStatus());

        response = _query.weather(nonExistentCode, "10");
        assertEquals(404, response.getStatus());
    }

    @Test
    public void testUpdateWeather() {
        DataPoint expectedWindDp = new DataPoint.Builder()
                .withCount(10).withFirst(10).withMedian(20).withLast(30).withMean(22).build();
        _update.updateWeather("BOS", "wind", _gson.toJson(expectedWindDp));
        _query.weather("BOS", "0").getEntity();

        String ping = _query.ping();
        JsonElement pingResult = new JsonParser().parse(ping);
        assertEquals(1, pingResult.getAsJsonObject().get("datasize").getAsInt());

        DataPoint expectedCloudCoverDp = new DataPoint.Builder()
                .withCount(4).withFirst(10).withMedian(60).withLast(100).withMean(50).build();
        _update.updateWeather("BOS", "cloudcover", _gson.toJson(expectedCloudCoverDp));

        List<AtmosphericInformation> ais = (List<AtmosphericInformation>) _query.weather("BOS", "0").getEntity();
        assertEquals(expectedWindDp, ais.get(0).getWind());
        assertEquals(expectedCloudCoverDp, ais.get(0).getCloudCover());
    }


    @Test
    public void testUpdateWeatherNonExistenAirport(){
        HashSet<String> airports = new HashSet<>(Arrays.asList("BOS", "EWR", "JFK", "LGA", "MMU"));
        assertList(airports);
        String nonExistentCode = "XYZ";
        assertFalse(airports.contains(nonExistentCode));

        Response response =  _update.updateWeather(
                nonExistentCode,
                DataPointType.WIND.name(),
                _gson.toJson(
                        new DataPoint.Builder()
                                .withCount(10).withFirst(10).withMedian(20).withLast(30).withMean(22).build())
        );
        assertEquals(404, response.getStatus());
    }

    @Test
    public void testUpdateWeatherInvalidInput() {
        Response response = _update.updateWeather("invalid", "invalid", "invalid");
        List<Error> errors = (List<Error>)response.getEntity();
        assertEquals(400, response.getStatus());
        assertEquals(
                Arrays.asList(
                    new Error(INVALID_VALUE, "iata", "The iata code is formed of 3 upper case letters"),
                        new Error(INVALID_VALUE, "pointType",
                                "The parameter must have one of the following values: " + Arrays.asList(DataPointType.values()) + " with upper or lower case"),
                        new Error(INVALID_VALUE, "dataPoint", "Data point has invalid structure")
                ),
                errors);
    }

    @Test
    public void testUpdateWeatherNullInput() {
        Response response = _update.updateWeather(null, null, null);
        List<Error> errors = (List<Error>)response.getEntity();
        assertEquals(400, response.getStatus());
        assertEquals(
                Arrays.asList(
                        new Error(MISSING_PARAMETER, "iata", "The parameter is mandatory"),
                        new Error(MISSING_PARAMETER, "pointType","The parameter is mandatory")
                ),
                errors);
    }

    private void assertList(Set<String> expected) {
        Response response = _update.getAirports();
        assertEquals(200, response.getStatus());
        Set<String> airports = (Set<String>) response.getEntity();
        assertEquals(expected, airports);
    }

}