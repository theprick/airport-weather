package com.crossover.trial.weather.validation;

import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.model.DataPointType;
import com.crossover.trial.weather.validation.generic.Error;
import com.crossover.trial.weather.validation.generic.ValidationRule;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.ArrayList;
import java.util.List;

import static com.crossover.trial.weather.validation.generic.ErrorCode.INVALID_VALUE;

/**
 * Created by Popescu Adrian-Dumitru on 06.09.2017.
 */
public class DataPointValidationRule extends ValidationRule {

    //shared gson json to object factory
    private final static Gson gson = new Gson();

    private DataPointTypeValidationRule dataPointTypeValidationRule = new DataPointTypeValidationRule();

    @Override
    public String parameterName() {
        return "dataPoint";
    }

    @Override
    public List<Error> apply(Object obj) {
        DataPointWithType dataPointWithType = (DataPointWithType) obj;
        List<Error> errors = new ArrayList<>();
        try {
            DataPoint dataPoint = gson.fromJson(dataPointWithType.getDataPointJson(), DataPoint.class);
            DataPointType dataPointType = parsePointType(dataPointWithType.getDataPointType(), errors);
            if(dataPointType != null) {
                validateDataPoint(dataPoint, dataPointType, errors);
            }
        } catch (JsonSyntaxException ex) {
            errors.add(new Error(INVALID_VALUE, "dataPoint", ""));
        }
        return errors;
    }

    private DataPointType parsePointType(String str, List<Error> allErrors) {
        List<Error> errors = dataPointTypeValidationRule.apply(str);
        if(errors != null){
            allErrors.addAll(errors);
            return null;
        } else {
            return DataPointType.valueOf(str.toUpperCase());
        }
    }

    private void validateDataPoint(DataPoint dataPoint, DataPointType dataPointType, List<Error> errors) {
        switch (dataPointType) {
            case WIND:
                if (dataPoint.getMean() < 0) {
                    errors.add(new Error(INVALID_VALUE, "mean", "Mean must by a decimal number greater than 0"));
                }
                break;
            case TEMPERATURE:
                if (dataPoint.getMean() < -50 || dataPoint.getMean() >= 100) {
                    errors.add(new Error(INVALID_VALUE, "mean", "Mean must by a decimal number between -50 and 100"));
                }
                break;
            case PRESSURE:
                if (dataPoint.getMean() < 650 || dataPoint.getMean() >= 800) {
                    errors.add(new Error(INVALID_VALUE, "mean", "Mean must by a decimal number between 650 and 799"));
                }
                break;
            case HUMIDTY:
            case PRECIPITATION:
            case CLOUDCOVER:
                if (dataPoint.getMean() < 0 || dataPoint.getMean() >= 100) {
                    errors.add(new Error(INVALID_VALUE, "mean", "Mean must by a decimal number between 0 and 99"));
                }
                break;
        }

        if(dataPoint.getFirst()<0) {
            errors.add(new Error(INVALID_VALUE, "first", "First must by an integer greater or equal to 0"));
        }
        if(dataPoint.getSecond()<0) {
            errors.add(new Error(INVALID_VALUE, "second", "First must by an integer greater or equal to 0"));
        }
        if(dataPoint.getThird()<0) {
            errors.add(new Error(INVALID_VALUE, "third", "First must by an integer greater or equal to 0"));
        }
        if(dataPoint.getCount()<0) {
            errors.add(new Error(INVALID_VALUE, "count", "First must by an integer greater or equal to 0"));
        }
    }
}
