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
public class InformationDataStore {

    private volatile static InformationDataStore instance;

    /**
     * atmospheric information for each airport based on iata code
     */
    private final ConcurrentHashMap<AirportData, AtomicReference<AtmosphericInformation>> atmosphericInformationForAirports;

    private InformationDataStore() {
        atmosphericInformationForAirports = new ConcurrentHashMap<>();
    }

    public static InformationDataStore getInstance() {
        if (instance == null) {
            synchronized (InformationDataStore.class) {
                if (instance == null) {
                    instance = new InformationDataStore();
                }
            }
        }

        return instance;
    }

    public Set<String> listAirportIataCodes() {
        return Collections.unmodifiableSet(
                StreamUtils.enumerationAsStream(atmosphericInformationForAirports.keys())
                        .map(AirportData::getIata).collect(Collectors.toSet()));
    }

    public Set<AirportData> listAirports() {
        return Collections.unmodifiableSet(
                StreamUtils.enumerationAsStream(atmosphericInformationForAirports.keys())
                        .collect(Collectors.toSet()));
    }

    public AirportData findAirportData(String iataCode) {
        return atmosphericInformationForAirports.searchKeys(
                Runtime.getRuntime().availableProcessors(), ad -> ad.getIata().equals(iataCode) ? ad : null);
    }

    public AtmosphericInformation findAtmosphericInformation(AirportData airportData) {
        return atmosphericInformationForAirports.getOrDefault(airportData,
                new AtomicReference<>(new AtmosphericInformation.Builder().build())).get();
    }

    public List<AtmosphericInformation> listAtmosphericInformation() {
        return Collections.unmodifiableList(
                atmosphericInformationForAirports.values().stream().map(AtomicReference::get).collect(Collectors.toList()));
    }

    public AirportData addAirport(AirportData airportData) {
        atmosphericInformationForAirports.put(airportData, new AtomicReference<>(new AtmosphericInformation.Builder().build()));
        return airportData;
    }

    public void clear() {
        atmosphericInformationForAirports.clear();
    }

    /**
     * Delete the airport identified by iata code and atmospheric information relate to it.
     *
     * @param iataCode iata code
     * @return deleted airport data or null if nothing is deleted
     */
    public AirportData deleteAirport(String iataCode) {
        AirportData airportData = findAirportData(iataCode);
        if (airportData != null) {
            atmosphericInformationForAirports.remove(airportData);
        }
        return airportData;
    }

    public void updateDataPoint(AirportData airportData, AtmosphericInformation newInfo) {
        AtomicReference<AtmosphericInformation> currentInfo = atmosphericInformationForAirports.get(airportData);
        if (currentInfo != null) {
            currentInfo.getAndAccumulate(newInfo, AtmosphericInformation::merge);
        }
    }

    public int size() {
        return atmosphericInformationForAirports.size();
    }
}
