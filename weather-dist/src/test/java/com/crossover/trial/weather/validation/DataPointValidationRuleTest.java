package com.crossover.trial.weather.validation;

import com.crossover.trial.weather.model.DataPoint;
import com.crossover.trial.weather.model.DataPointType;
import com.crossover.trial.weather.validation.generic.Error;
import com.crossover.trial.weather.validation.generic.ErrorCode;
import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by Popescu Adrian-Dumitru on 07.09.2017.
 *
 * Tests for {@link DataPointValidationRule}
 */
public class DataPointValidationRuleTest {

    private Gson _gson = new Gson();

    private DataPointValidationRule dataPointValidationRule = new DataPointValidationRule();

    @Test
    public void testInvalidWind() {
        testInvalidMean(DataPointType.WIND, -10,
                new Error(ErrorCode.INVALID_VALUE, "mean", "Mean must by a decimal number greater than 0"));
    }

    @Test
    public void testInvalidTemperature() {
        testInvalidMean(DataPointType.TEMPERATURE, -51,
                new Error(ErrorCode.INVALID_VALUE, "mean", "Mean must by a decimal number between -50 and 99"));
        testInvalidMean(DataPointType.TEMPERATURE, 100,
                new Error(ErrorCode.INVALID_VALUE, "mean", "Mean must by a decimal number between -50 and 99"));
    }

    @Test
    public void testInvalidPressure() {
        testInvalidMean(DataPointType.PRESSURE, 649,
                new Error(ErrorCode.INVALID_VALUE, "mean", "Mean must by a decimal number between 650 and 799"));
        testInvalidMean(DataPointType.PRESSURE, 800,
                new Error(ErrorCode.INVALID_VALUE, "mean", "Mean must by a decimal number between 650 and 799"));
    }

    @Test
    public void testInvalidHumidity() {
        testInvalidMean(DataPointType.HUMIDTY, -1,
                new Error(ErrorCode.INVALID_VALUE, "mean", "Mean must by a decimal number between 0 and 99"));
        testInvalidMean(DataPointType.HUMIDTY, 100,
                new Error(ErrorCode.INVALID_VALUE, "mean", "Mean must by a decimal number between 0 and 99"));
    }

    @Test
    public void testInvalidPrecipitation() {
        testInvalidMean(DataPointType.HUMIDTY, -1,
                new Error(ErrorCode.INVALID_VALUE, "mean", "Mean must by a decimal number between 0 and 99"));
        testInvalidMean(DataPointType.HUMIDTY, 100,
                new Error(ErrorCode.INVALID_VALUE, "mean", "Mean must by a decimal number between 0 and 99"));
    }

    @Test
    public void testInvalidCloudcover() {
        testInvalidMean(DataPointType.HUMIDTY, -1,
                new Error(ErrorCode.INVALID_VALUE, "mean", "Mean must by a decimal number between 0 and 99"));
        testInvalidMean(DataPointType.HUMIDTY, 100,
                new Error(ErrorCode.INVALID_VALUE, "mean", "Mean must by a decimal number between 0 and 99"));
    }

    private void testInvalidMean(DataPointType dataPointType, double mean, Error expectedError) {
        DataPoint.Builder builder = new DataPoint.Builder().withFirst(10).withMedian(20).withLast(30).withCount(10);
        List<Error> errors = dataPointValidationRule.apply(
                new DataPointWithType(dataPointType.name(), _gson.toJson(builder.withMean(mean).build())));
        Assert.assertEquals(1, errors.size());
        Assert.assertEquals(expectedError, errors.get(0));
    }
}
