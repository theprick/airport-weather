package com.crossover.trial.weather.validation.generic;

import java.util.List;
import java.util.function.Function;

/**
 * Created by Popescu Adrian-Dumitru on 05.09.2017.
 */
public abstract class ValidationRule implements Function<Object, List<Error>> {

    public abstract String parameterName();
}
