package com.crossover.trial.weather.validation;

import com.crossover.trial.weather.model.DataPointType;

/**
 * Created by Popescu Adrian-Dumitru on 06.09.2017.
 * Holds the type of the data point and the actual data point.
 */
public class DataPointWithType {
    private String dataPointType;
    private String dataPointJson;

    public DataPointWithType(String dataPointType, String dataPointJson) {
        this.dataPointType = dataPointType;
        this.dataPointJson = dataPointJson;
    }

    public String getDataPointType() {
        return dataPointType;
    }

    public void setDataPointType(String dataPointType) {
        this.dataPointType = dataPointType;
    }

    public String getDataPointJson() {
        return dataPointJson;
    }

    public void setDataPointJson(String dataPointJson) {
        this.dataPointJson = dataPointJson;
    }
}
