package com.crossover.trial.weather.utils;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by Popescu Adrian-Dumitru on 03.09.2017.
 */
public class StreamUtils {

    public static <T> Stream<T> enumerationAsStream(Enumeration<T> e) {
        return StreamSupport.stream(Spliterators.spliteratorUnknownSize(
                new Iterator<T>() {
                    @Override
                    public boolean hasNext() {
                        return e.hasMoreElements();
                    }

                    @Override
                    public T next() {
                        return e.nextElement();
                    }
                }, Spliterator.ORDERED), false);
    }
}
