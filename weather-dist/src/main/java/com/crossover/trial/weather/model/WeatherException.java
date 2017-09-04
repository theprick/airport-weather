package com.crossover.trial.weather.model;

/**
 * An internal exception marker
 */
public class WeatherException extends RuntimeException {
    public WeatherException(String msg) {
        super(msg);
    }
}
