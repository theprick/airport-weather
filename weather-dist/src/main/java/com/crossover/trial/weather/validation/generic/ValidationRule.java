package com.crossover.trial.weather.validation.generic;

import java.util.List;
import java.util.function.Function;

/**
 * Created by Popescu Adrian-Dumitru on 05.09.2017.
 *
 * Generic class to be implemented by actual validation rules implementations.
 * Receives an {@link Object} that is the actual value of the parameter, executes the validation logic
 * from Function{@link #apply(Object)} and returns a {@link List<Error>}.
 *
 */
public abstract class ValidationRule implements Function<Object, List<Error>> {

    public abstract String parameterName();
}
