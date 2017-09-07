package com.crossover.trial.weather.data;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Popescu Adrian-Dumitru on 03.09.2017.
 *
 * Internal performance counter to better understand most requested information, this map can be improved but
 * for now provides the basis for future performance optimizations. Due to the stateless deployment architecture
 * we don't want to write this to disk, but will pull it off using a REST request and aggregate with other
 * performance metrics {@link com.crossover.trial.weather.endpoint.WeatherQueryEndpoint#ping()}
 */
public class FrequencyDataStore {

    private volatile static FrequencyDataStore instance;

    private final Map<String, AtomicInteger> requestFrequency;

    private final Map<Double, AtomicInteger> radiusFrequency;

    private final Object requestsLock = new Object();

    private final Object radiusLock = new Object();

    private FrequencyDataStore() {
        requestFrequency = new ConcurrentHashMap<>();
        radiusFrequency = new ConcurrentHashMap<>();
    }

    public static FrequencyDataStore getInstance() {
        if(instance == null) {
            synchronized (FrequencyDataStore.class) {
                if(instance == null) {
                    instance = new FrequencyDataStore();
                }
            }
        }

        return instance;
    }

    public void updateRequestFrequency(String iataCode) {
        synchronized (requestsLock) {
            requestFrequency.computeIfAbsent(iataCode, k -> new AtomicInteger(0));
        }
        requestFrequency.get(iataCode).incrementAndGet();
    }

    public void updateRadiusFrequency(Double radius) {
        synchronized (radiusLock) {
            radiusFrequency.computeIfAbsent(radius, k -> new AtomicInteger(0));
        }
        radiusFrequency.get(radius).incrementAndGet();
    }

    public int getRequestFrequency(String iata){
        return requestFrequency.getOrDefault(iata, new AtomicInteger(0)).intValue();
    }

    public int getRadiusFrequency(Double radius){
        return radiusFrequency.getOrDefault(radius, new AtomicInteger(0)).intValue();
    }

    public int sizeOfRequestFrequency() {
        return requestFrequency.size();
    }

    public Set<Double> listRadii() {
        return Collections.unmodifiableSet(radiusFrequency.keySet());
    }

    public void clear() {
        radiusFrequency.clear();
        requestFrequency.clear();
    }

    public int totalNumberOfRequests() {
        return requestFrequency.values().stream().mapToInt(AtomicInteger::intValue).sum();
    }

    public void deleteAirport(String iataCode) {
        requestFrequency.remove(iataCode);
    }
}
