package com.crossover.trial.weather.validation.generic;

import com.crossover.trial.weather.validation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Popescu Adrian-Dumitru on 06.09.2017.
 */
public class ValidationRuleFactory {

    private static List<? extends ValidationRule> validationRules = Arrays.asList(
            new DataPointTypeValidationRule(),
            new DataPointValidationRule(),
            new IataValidationRule(),
            new LatitudeValidationRule(),
            new LongitudeValidationRule(),
            new RadiusValidationRule()
    );

    public ValidationRule getValidationRule(String parameterName) {
        return validationRules.stream().filter(vr -> vr.parameterName().equals(parameterName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Rule for " + parameterName + "not found"));
    }
}
