package com.crossover.trial.weather.validation.generic;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Popescu Adrian-Dumitru on 05.09.2017.
 *
 * Generic class used to validate the input parameters
 */
public class GenericInputRequestValidator {

    private ValidationRuleFactory validationRuleFactory = new ValidationRuleFactory();

    public void validate(List<String> paramaterNames, List<Object> parameterValues) throws InputValidationException {
        List<Error> allErrors = new ArrayList<>();
        for (int i=0; i<paramaterNames.size(); i++) {
            ValidationRule validationRule = validationRuleFactory.getValidationRule(paramaterNames.get(i));
            List<Error> errors = validationRule.apply(parameterValues.get(i));
            if(errors != null) {
                allErrors.addAll(errors);
            }
        }

        if(!allErrors.isEmpty()) {
            throw new InputValidationException(allErrors);
        }
    }
}
