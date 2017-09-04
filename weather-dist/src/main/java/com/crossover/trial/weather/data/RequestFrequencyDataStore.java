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
public class RequestFrequencyDataStore {

    private volatile static RequestFrequencyDataStore instance;

    private final Map<String, AtomicInteger> requestFrequency;

    private final Map<Double, AtomicInteger> radiusFrequency;

    private final Object requestFrequencytLock = new Object();

    private final Object requestRadiusLock = new Object();

    private RequestFrequencyDataStore() {
        requestFrequency = new ConcurrentHashMap<String, AtomicInteger>();
        radiusFrequency = new ConcurrentHashMap<Double, AtomicInteger>();
    }

    public static RequestFrequencyDataStore getInstance() {
        if(instance == null) {
            synchronized (RequestFrequencyDataStore.class) {
                if(instance == null) {
                    instance = new RequestFrequencyDataStore();
                }
            }
        }

        return instance;
    }

    public void updateRequestFrequency(String iataCode) {
        AtomicInteger atomicInt;
        synchronized (requestFrequencytLock) {
            atomicInt = requestFrequency.get(iataCode);
            if (atomicInt == null) {
                requestFrequency.put(iataCode, new AtomicInteger(1));
            }
        }
        requestFrequency.get(iataCode).incrementAndGet();
    }

    public void updateRadiusFrequency(Double radius) {
        AtomicInteger atomicInteger;
        synchronized (requestRadiusLock) {
            atomicInteger = radiusFrequency.get(radius);
            if (atomicInteger == null) {
                radiusFrequency.put(radius, new AtomicInteger(1));
            }
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


//        requestFrequency.merge(iataCode, 1, Integer::sum);
//        radiusFrequency.merge(radius, 1, Integer::sum);
//
//
//    public static void main(String[] args) {
//        AtomicInteger atomicInteger = new AtomicInteger(9);
//        int a = atomicInteger.incrementAndGet();
//        System.out.println(atomicInteger.toString());
//    }
}
