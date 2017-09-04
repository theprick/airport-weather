package com.crossover.trial.weather.model;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Basic airport information.
 *
 * @author code test administrator
 */
public class AirportData {

    /** The full name of the airport **/
    private String name;

    /** Main city served by airport. May be spelled differently from name **/
    private String city;

    /** Country or territory where airport is located **/
    private String country;

    /** the three letter IATA code */
    private String iata;

    /** the four letter ICAO code */
    private String icao;

    /** latitude value in degrees */
    private double latitude;

    /** longitude value in degrees */
    private double longitude;

    /** In feet **/
    private double altitude;

    /** Hours offset from UTC. Fractional hours are expressed as decimals. (e.g. India is 5.5) **/
    private double timezone;

    /** One of E (Europe), A (US/Canada), S (South America), O (Australia), Z (New Zealand), N (None) or U (Unknown) **/
    private DST dst;

    public AirportData() { }

    /**
     * @param iataCode 3 letter code
     * @param latitude in degrees
     * @param longitude in degrees
     *
     */
    private AirportData(String iataCode, double latitude, double longitude) {
        this.iata = iataCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    private AirportData(String name, String city, String country, String iata, String icao, double latitude, double longitude, double altitude, double timezone, DST dst) {
        this.name = name;
        this.city = city;
        this.country = country;
        this.iata = iata;
        this.icao = icao;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.timezone = timezone;
        this.dst = dst;
    }

    public String getIata() {
        return iata;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getIcao() {
        return icao;
    }

    public double getAltitude() {
        return altitude;
    }

    public double getTimezone() {
        return timezone;
    }

    public DST getDst() {
        return dst;
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.NO_CLASS_NAME_STYLE);
    }

    public boolean equals(Object other) {
        if (other instanceof AirportData) {
            return ((AirportData)other).getIata().equals(this.getIata());
        }

        return false;
    }

    public static class Builder {
        private String name;

        private String city;

        private String country;

        private String iata;

        private String icao;

        private double latitude;

        private double longitude;

        private double altitude;

        private double timezone;

        private DST dst;

        public Builder() { }

        public AirportData.Builder withName(String name) {
            this.name = name;
            return this;
        }

        public AirportData.Builder withCity(String city) {
            this.city = city;
            return this;
        }

        public AirportData.Builder withCountry(String country) {
            this.country = country;
            return this;
        }

        public AirportData.Builder withIata(String iata) {
            this.iata = iata;
            return this;
        }

        public AirportData.Builder withIcao(String icao) {
            this.icao = icao;
            return this;
        }

        public AirportData.Builder withLatitude(double latitude) {
            this.latitude = latitude;
            return this;
        }

        public AirportData.Builder withLongitude(double longitude) {
            this.longitude = longitude;
            return this;
        }

        public AirportData.Builder withAltitude(double altitude) {
            this.altitude = altitude;
            return this;
        }

        public AirportData.Builder withTimezone(double timezone) {
            this.timezone = timezone;
            return this;
        }

        public AirportData.Builder withDst(char c) {
            this.dst = DST.valueOf(c);
            return this;
        }

        public AirportData build() {
            if(iata == null && latitude == 0.0 && longitude == 0.0) {
                throw new IllegalArgumentException("iata, latitude and longitude are mandatory");
            }
            return new AirportData(name, city, country, iata, icao, latitude, longitude, altitude, timezone, dst);
        }
    }
}
