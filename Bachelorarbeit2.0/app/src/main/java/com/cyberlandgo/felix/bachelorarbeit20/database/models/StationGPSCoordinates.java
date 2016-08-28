package com.cyberlandgo.felix.bachelorarbeit20.database.models;

/**
 * Created by Felix on 06.07.2016.
 */
public class StationGPSCoordinates
{
    private long _id;
    String _station;
    String _latitude;
    String _longitude;

    public StationGPSCoordinates()
    {

    }

    public StationGPSCoordinates(String station, String latitude, String longitude)
    {
        this._station = station;
        this._latitude = latitude;
        this._longitude = longitude;


    }

    public void setId(long id)
    {
        this._id = id;
    }

    public long getId()
    {
        return this._id;
    }

    public void setStation(String station)
    {
        this._station = station;
    }

    public String getStation()
    {
        return this._station;
    }

    public void setLatitude(String latitude)
    {
        this._latitude = latitude;
    }

    public String getLatitude()
    {
        return this._latitude;
    }

    public void setLongitude(String longitude)
    {
        this._longitude = _longitude;
    }

    public String getLongitude()
    {
        return this._longitude;
    }
}
