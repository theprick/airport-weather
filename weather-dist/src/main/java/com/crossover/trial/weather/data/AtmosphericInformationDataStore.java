package com.crossover.trial.weather.data;

import com.crossover.trial.weather.model.AirportData;
import com.crossover.trial.weather.model.AtmosphericInformation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Popescu Adrian-Dumitru on 03.09.2017.
 *
 * My data store with airports and atmospheric information stored in memory and shared globally for all application
 */
public class AtmosphericInformationDataStore {

    private volatile static AtmosphericInformationDataStore instance;

    /** atmospheric information for each airport based on iata code */
    private final ConcurrentHashMap<String, AtmosphericInformation> atmosphericInformationForAirportData;

    /** all known airports */
    private final ConcurrentHashMap<String, AirportData> codesForAirportData;

    private AtmosphericInformationDataStore() {
        atmosphericInformationForAirportData = new ConcurrentHashMap<String, AtmosphericInformation>();
        codesForAirportData = new ConcurrentHashMap<String, AirportData>();
    }

    public static AtmosphericInformationDataStore getInstance() {
        if(instance == null) {
            synchronized (AtmosphericInformationDataStore.class) {
                if(instance == null) {
                    instance = new AtmosphericInformationDataStore();
                }
            }
        }

        return instance;
    }

    public Set<String> listAirportIataCodes() {
        return Collections.unmodifiableSet(new HashSet<>(Collections.list(codesForAirportData.keys())));
    }

    public List<AirportData> listAirports() {
        return Collections.unmodifiableList(new ArrayList<>(codesForAirportData.values()));
    }

    public AirportData findAirportData(String iataCode) {
        return codesForAirportData.get(iataCode);
    }

    public AtmosphericInformation findAtmosphericInformation(String iataCode) {
        return atmosphericInformationForAirportData.getOrDefault(iataCode, new AtmosphericInformation.Builder().build());
    }

    public List<AtmosphericInformation> listAtmosphericInformation() {
        return Collections.unmodifiableList(new ArrayList<>(atmosphericInformationForAirportData.values()));
    }

    public AirportData addAirport(AirportData airportData) {
        synchronized (AtmosphericInformationDataStore.class) {
            AirportData addedData = codesForAirportData.put(airportData.getIata(), airportData);
            atmosphericInformationForAirportData.put(airportData.getIata(), new AtmosphericInformation.Builder().build());
            return addedData;
        }
    }

    public int size() {
        return codesForAirportData.size();
    }

    public void clear() {
        atmosphericInformationForAirportData.clear();
        codesForAirportData.clear();
    }

    public AtmosphericInformation addAtmosphericInformation(String iataCode, AtmosphericInformation atmosphericInformation) {
        if(codesForAirportData.get(iataCode) == null) {
            throw new IllegalArgumentException("IATA code " + iataCode + " not found");
        }
        return atmosphericInformationForAirportData.put(iataCode, atmosphericInformation);
    }
}
