package com.crossover.trial.weather.data;

import com.crossover.trial.weather.model.*;
import com.crossover.trial.weather.utils.StreamUtils;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Created by Popescu Adrian-Dumitru on 03.09.2017.
 * <p>
 * My data store with airports and atmospheric information stored in memory and shared globally for all application
 */
public class AirportsDataStore {

    private volatile static AirportsDataStore instance;

    /**
     * atmospheric information for each airport based on iata code
     */
    private final ConcurrentHashMap<AirportData, AtomicReference<AtmosphericInformation>> atmosphericInformationForAirportData;

    private AirportsDataStore() {
        atmosphericInformationForAirportData = new ConcurrentHashMap<>();
    }

    public static AirportsDataStore getInstance() {
        if (instance == null) {
            synchronized (AirportsDataStore.class) {
                if (instance == null) {
                    instance = new AirportsDataStore();
                }
            }
        }

        return instance;
    }

    public Set<String> listAirportIataCodes() {
        return Collections.unmodifiableSet(
                StreamUtils.enumerationAsStream(atmosphericInformationForAirportData.keys())
                        .map(AirportData::getIata).collect(Collectors.toSet()));
    }

    public Set<AirportData> listAirports() {
        return Collections.unmodifiableSet(
                StreamUtils.enumerationAsStream(atmosphericInformationForAirportData.keys())
                        .collect(Collectors.toSet()));
    }

    public AirportData findAirportData(String iataCode) {
        return atmosphericInformationForAirportData.searchKeys(
                Runtime.getRuntime().availableProcessors(), ad -> ad.getIata().equals(iataCode) ? ad : null);
    }

    public AtmosphericInformation findAtmosphericInformation(AirportData airportData) {
        return atmosphericInformationForAirportData.getOrDefault(airportData,
                new AtomicReference<>(new AtmosphericInformation.Builder().build())).get();
    }

    public List<AtmosphericInformation> listAtmosphericInformation() {
        return Collections.unmodifiableList(
                atmosphericInformationForAirportData.values().stream().map(AtomicReference::get).collect(Collectors.toList()));
    }

    public AirportData addAirport(AirportData airportData) {
        atmosphericInformationForAirportData.put(airportData, new AtomicReference<>(new AtmosphericInformation.Builder().build()));
        return airportData;
    }

    public void clear() {
        atmosphericInformationForAirportData.clear();
    }

    /**
     * Delete the airport identified by iata code and atmospheric information relate to it.
     *
     * @param iataCode iata code
     * @return deleted airport data
     * @throws AirportNotFoundException if iataCode code doesn't exists in the data store
     */
    public AirportData deleteAirport(String iataCode) {
        AirportData airportData = findAirportData(iataCode);
        //FIXME needs work
        if (airportData == null) {
            throw new AirportNotFoundException("IATA code " + iataCode + " not found");
        }

        atmosphericInformationForAirportData.remove(airportData);

        return airportData;
    }

    public void updateDataPoint(AirportData airportData, AtmosphericInformation newInfo) {
        AtomicReference<AtmosphericInformation> currentInfo = atmosphericInformationForAirportData.get(airportData);
        if (currentInfo == null) {
            throw new WeatherException("IATA code " + airportData.getIata() + "not present in the data store");
        }
        currentInfo.getAndAccumulate(newInfo, AtmosphericInformation::merge);
    }

    /**
     * update atmospheric information with the given data point for the given point type
     *
     * @param ai        the atmospheric information object to update
     * @param pointType the data point type as a string
     * @param dp        the actual data point
     */
    private void updateAtmosphericInformation(AtmosphericInformation ai, String pointType, DataPoint dp) {
        final DataPointType dataPointType = DataPointType.valueOf(pointType.toUpperCase());

        if (dataPointType.equals(DataPointType.WIND)) {
            if (dp.getMean() >= 0) {
                ai.setWind(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        if (dataPointType.equals(DataPointType.TEMPERATURE)) {
            if (dp.getMean() >= -50 && dp.getMean() < 100) {
                ai.setTemperature(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        if (dataPointType.equals(DataPointType.HUMIDTY)) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setHumidity(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        if (dataPointType.equals(DataPointType.PRESSURE)) {
            if (dp.getMean() >= 650 && dp.getMean() < 800) {
                ai.setPressure(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        if (dataPointType.equals(DataPointType.CLOUDCOVER)) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setCloudCover(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }

        if (dataPointType.equals(DataPointType.PRECIPITATION)) {
            if (dp.getMean() >= 0 && dp.getMean() < 100) {
                ai.setPrecipitation(dp);
                ai.setLastUpdateTime(System.currentTimeMillis());
                return;
            }
        }
        throw new IllegalStateException("couldn't update atmospheric data");
    }
}
