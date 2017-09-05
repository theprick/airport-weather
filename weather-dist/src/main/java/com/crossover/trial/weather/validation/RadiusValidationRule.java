package com.crossover.trial.weather.validation;

import com.crossover.trial.weather.validation.generic.Error;
import com.crossover.trial.weather.validation.generic.ValidationRule;

import java.util.Collections;
import java.util.List;

import static com.crossover.trial.weather.validation.generic.ErrorCode.INVALID_VALUE;

/**
 * Created by Popescu Adrian-Dumitru on 05.09.2017.
 *
 * Encapsulates the validation logic for validating radius.
 */
public class RadiusValidationRule extends ValidationRule {

    @Override
    public List<Error> apply(Object param) {
        if(param == null) {
            return null;
        }

        Error error = new Error(INVALID_VALUE, parameterName(), "The parameter must be a valid decimal number greater or equal to 0");
        try {
            double radius = Double.parseDouble(String.valueOf(param));
            if(radius < 0) {
                return Collections.singletonList(error);
            }
        } catch (NumberFormatException e) {
            return Collections.singletonList(error);
        }
        return null;
    }

    @Override
    public String parameterName() {
        return "radius";
    }
}
