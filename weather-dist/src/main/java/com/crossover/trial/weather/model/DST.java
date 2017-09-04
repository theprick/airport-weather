package com.crossover.trial.weather.model;

import java.util.Arrays;

/**
 * Created by Popescu Adrian-Dumitru on 03.09.2017.
 */
public enum DST {
    Europe('E'),
    USCanada('A'),
    SouthAmerica('S'),
    Australia('O'),
    NewZeeland('Z'),
    None('N'),
    Unknown('U');

    private char val;

    DST(char val) {
        this.val = val;
    }

    private char getValue() {
        return val;
    }

    public static DST valueOf(char c) {
        return Arrays.stream(DST.values()).filter(dst -> dst.val == c).findFirst().orElse(Unknown);
    }
}
