package com.crossover.trial.weather.utils;

/**
 * Created by Popescu Adrian-Dumitru on 04.09.2017.
 */
public class StringUtils {

    public static String removeQuotes(String s) {
        s = s.trim();

        if(s.isEmpty()) {
            return s;
        }

        if(s.startsWith("\"")) {
            s = s.substring(1);
        }

        if(s.isEmpty()) {
            return s;
        }

        if(s.endsWith("\"")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }
}
