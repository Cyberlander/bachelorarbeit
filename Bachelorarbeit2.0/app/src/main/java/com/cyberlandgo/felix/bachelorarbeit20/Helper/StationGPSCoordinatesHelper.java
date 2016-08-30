package com.cyberlandgo.felix.bachelorarbeit20.Helper;

import android.content.Context;

import com.cyberlandgo.felix.bachelorarbeit20.database.datasources.GPSCoordinatesDatasource;
import com.cyberlandgo.felix.bachelorarbeit20.database.models.StationGPSCoordinates;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Felix on 30.08.2016.
 */
public class StationGPSCoordinatesHelper
{

    public static String getCoordinatesForStation(Context context, String station)
    {
        GPSCoordinatesDatasource gpsCoordinatesDatasource = new GPSCoordinatesDatasource(context);
        gpsCoordinatesDatasource.open();
        ArrayList<StationGPSCoordinates> stationGPSCoordinatesList = gpsCoordinatesDatasource.getAllStationGPSCoordinates();
        HashMap<String,String> stationGPSCoordinatesMap = new HashMap<>();

        for (StationGPSCoordinates s : stationGPSCoordinatesList)
        {

            String currentStation = s.getStation();
            String latitude = s.getLatitude();
            String longitude = s.getLongitude();

            stationGPSCoordinatesMap.put(currentStation,latitude + ":" + longitude);
        }

        return stationGPSCoordinatesMap.get(station);
    }
}
