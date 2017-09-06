package com.crossover.trial.weather;

import com.crossover.trial.weather.data.FrequencyDataStore;
import com.crossover.trial.weather.data.InformationDataStore;
import com.crossover.trial.weather.model.AirportData;

/**
 * Created by Popescu Adrian-Dumitru on 06.09.2017.
 *
 */
public class TestUtils {

    /**
     * A dummy init method that loads hard coded data
     */
    public static void init() {
        InformationDataStore.getInstance().clear();
        FrequencyDataStore.getInstance().clear();

        InformationDataStore.getInstance().addAirport(
                new AirportData.Builder().withIata("BOS").withLatitude(42.364347).withLongitude(-71.005181).build());
        InformationDataStore.getInstance().addAirport(
                new AirportData.Builder().withIata("EWR").withLatitude(40.6925).withLongitude(-74.168667).build());
        InformationDataStore.getInstance().addAirport(
                new AirportData.Builder().withIata("JFK").withLatitude(40.639751).withLongitude(-73.778925).build());
        InformationDataStore.getInstance().addAirport(
                new AirportData.Builder().withIata("LGA").withLatitude(40.777245).withLongitude(-73.872608).build());
        InformationDataStore.getInstance().addAirport(
                new AirportData.Builder().withIata("MMU").withLatitude(40.79935).withLongitude(-74.4148747).build());
    }
}
