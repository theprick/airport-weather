package com.crossover.trial.weather.validation;

/**
 * Created by Popescu Adrian-Dumitru on 05.09.2017.
 *
 * Encapsulates the validation logic for validating latitude.
 */
public class LatitudeValidationRule extends LongitudeValidationRule {

    @Override
    public String parameterName() {
        return "latitude";
    }
}
