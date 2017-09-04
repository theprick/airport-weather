package com.crossover.trial.weather.model;

/**
 * Created by Popescu Adrian-Dumitru on 04.09.2017.
 *
 * Thrown when the airport couldn't be identified from the information provided
 */
public class AirportNotFoundException extends RuntimeException {

    public AirportNotFoundException(String message) {
        super(message);
    }
}
