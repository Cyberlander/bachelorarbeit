package com.cyberlandgo.felix.bachelorarbeit20.database.models;

/**
 * Created by Felix on 02.05.2016.
 */
public class Station {
    private long id;
    private String stationName;
    private String major;
    private String minor;

    public Station()
    {
    }

    public Station(String stationName, String major, String minor)
    {
        this.stationName = stationName;
        this.major = major;
        this.minor = minor;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String product) {
        this.stationName = product;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getMinor() {
        return minor;
    }

    public void setMinor(String minor) {
        this.minor = minor;
    }

    public String toString()
    {
        return String.format("Station[id=%d , major=%s ,minor=%s , stationName=%s ]", id, major,minor,stationName);
    }


}
