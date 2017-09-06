package com.crossover.trial.weather.validation;

import com.crossover.trial.weather.validation.generic.Error;
import com.crossover.trial.weather.validation.generic.ValidationRule;

import java.util.Collections;
import java.util.List;

import static com.crossover.trial.weather.validation.generic.ErrorCode.INVALID_VALUE;
import static com.crossover.trial.weather.validation.generic.ErrorCode.MISSING_PARAMETER;

/**
 * Created by Popescu Adrian-Dumitru on 05.09.2017.
 *
 * Encapsulates the validation logic for validating longitude.
 */
public class LongitudeValidationRule extends ValidationRule {

    @Override
    public List<Error> apply(Object param) {
        if(param == null ) {
            return Collections.singletonList(new Error(MISSING_PARAMETER, parameterName(), "The parameter is mandatory"));
        }
        try {
            Double.parseDouble(String.valueOf(param));
        } catch (NumberFormatException e) {
            return Collections.singletonList(new Error(INVALID_VALUE, parameterName(), "The parameter must be a valid decimal number"));
        }
        return null;
    }

    @Override
    public String parameterName() {
        return "long";
    }
}
