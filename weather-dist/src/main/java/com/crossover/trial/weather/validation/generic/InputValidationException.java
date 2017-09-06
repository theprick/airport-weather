package com.crossover.trial.weather.validation.generic;

import java.util.List;

/**
 * Created by Popescu Adrian-Dumitru on 05.09.2017.
 * Custom validation exception.
 */
public class InputValidationException extends Throwable {

    private List<Error> errors;

    InputValidationException(List<Error> errors) {
        this.errors = errors;
    }

    public List<Error> getErrors() {
        return errors;
    }
}
