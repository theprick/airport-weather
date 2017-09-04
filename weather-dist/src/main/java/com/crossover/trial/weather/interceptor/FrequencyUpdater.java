package com.crossover.trial.weather.interceptor;

import javax.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by Popescu Adrian-Dumitru on 03.09.2017.
 * Annotation used to bind {@link WeatherQueryFilter} to method
 * {@link com.crossover.trial.weather.endpoint.WeatherQueryEndpoint#weather(String, String)}
 *
 */
@NameBinding
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface FrequencyUpdater { }
