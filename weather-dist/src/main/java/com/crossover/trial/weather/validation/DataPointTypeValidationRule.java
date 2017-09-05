package com.crossover.trial.weather.validation;

import com.crossover.trial.weather.model.DataPointType;
import com.crossover.trial.weather.validation.generic.Error;
import com.crossover.trial.weather.validation.generic.ValidationRule;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.crossover.trial.weather.validation.generic.ErrorCode.INVALID_VALUE;
import static com.crossover.trial.weather.validation.generic.ErrorCode.MISSING_PARAMETER;

/**
 * Created by Popescu Adrian-Dumitru on 05.09.2017.
 * <p>
 * Encapsulates the validation logic for validating data point type.
 */
public class DataPointTypeValidationRule extends ValidationRule {

    @Override
    public List<Error> apply(Object param) {
        if(param == null) {
            return  Collections.singletonList(new Error(MISSING_PARAMETER, parameterName(), "The parameter is mandatory"));
        }
        try {
            DataPointType.valueOf(String.valueOf(param).toUpperCase());
        } catch (IllegalArgumentException ex) {
            return  Collections.singletonList(new Error(INVALID_VALUE, parameterName(),
                    "The parameter must have one of the following values: " + Arrays.asList(DataPointType.values())));
        }
        return null;
    }

    @Override
    public String parameterName() {
        return "pointType";
    }
}
