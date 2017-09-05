package com.crossover.trial.weather.endpoint;

import com.crossover.trial.weather.endpoint.RestWeatherCollectorEndpoint;
import com.crossover.trial.weather.endpoint.RestWeatherQueryEndpoint;
import com.crossover.trial.weather.endpoint.WeatherCollectorEndpoint;
import com.crossover.trial.weather.endpoint.WeatherQueryEndpoint;
import com.crossover.trial.weather.model.AtmosphericInformation;
import com.crossover.trial.weather.model.DataPoint;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class WeatherEndpointTest {

    private WeatherQueryEndpoint _query = new RestWeatherQueryEndpoint();

    private WeatherCollectorEndpoint _update = new RestWeatherCollectorEndpoint();

    private Gson _gson = new Gson();

    private DataPoint _dp;

    @Before
    public void setUp() throws Exception {
        RestWeatherQueryEndpoint.init();
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
    public void testGetNearby() throws Exception {
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
    public void testUpdate() throws Exception {
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
}