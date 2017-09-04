package com.crossover.trial.weather.model;

/**
 * Created by Popescu Adrian-Dumitru on 04.09.2017.
 *
 * Creates {@link AtmosphericInformation} objects
 */
public class AtmosphericInformationFactory {

    /**
     * Creates {@link AtmosphericInformation} objects initialized based on {@link DataPointType} and {@link DataPoint} values
     *
     * @param dataPointType the field to be initialized
     * @param dp the values
     *
     * @return a new initialized object
     * @throws IllegalArgumentException if dataPointType is not valid
     */
    public AtmosphericInformation getAtmosphericInformation(DataPointType dataPointType, DataPoint dp) {
        AtmosphericInformation ai = new AtmosphericInformation.Builder().build();
        if (dataPointType.equals(DataPointType.WIND)) {
            if (dp.getMean() >= 0) {
                ai.setWind(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return ai;
            }
        }

        if (dataPointType.equals(DataPointType.TEMPERATURE)) {
            if (dp.getMean() >= -50 && dp.getMean() < 100) {
                ai.setTemperature(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return ai;
            }
        }

        if (dataPointType.equals(DataPointType.HUMIDTY)) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setHumidity(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return ai;
            }
        }

        if (dataPointType.equals(DataPointType.PRESSURE)) {
            if (dp.getMean() >= 650 && dp.getMean() < 800) {
                ai.setPressure(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return ai;
            }
        }

        if (dataPointType.equals(DataPointType.CLOUDCOVER)) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setCloudCover(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return ai;
            }
        }

        if (dataPointType.equals(DataPointType.PRECIPITATION)) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setPrecipitation(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return ai;
            }
        }
        throw new IllegalArgumentException(dataPointType + " not handled");
    }
}
