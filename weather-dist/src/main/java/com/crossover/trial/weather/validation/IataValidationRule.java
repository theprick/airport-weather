package com.crossover.trial.weather.validation;

import com.crossover.trial.weather.validation.generic.Error;
import com.crossover.trial.weather.validation.generic.ErrorCode;
import com.crossover.trial.weather.validation.generic.ValidationRule;

import java.util.Collections;
import java.util.List;

import static com.crossover.trial.weather.validation.generic.ErrorCode.INVALID_VALUE;
import static com.crossover.trial.weather.validation.generic.ErrorCode.MISSING_PARAMETER;

/**
 * Created by Popescu Adrian-Dumitru on 05.09.2017.
 *
 * Encapsulates the validation logic for validating iata code.
 */
public class IataValidationRule extends ValidationRule {

    @Override
    public List<Error> apply(Object param) {
        if(param == null ) {
            return Collections.singletonList(new Error(MISSING_PARAMETER, parameterName(), "The parameter is mandatory"));
        }
        String iata = String.valueOf(param);
        if(!iata.matches("[A-Z]{3}")) {
            return Collections.singletonList(new Error(INVALID_VALUE, parameterName(), "The iata code is formed of 3 upper case letters"));
        }
        return null;
    }

    @Override
    public String parameterName() {
        return "iata";
    }
}
