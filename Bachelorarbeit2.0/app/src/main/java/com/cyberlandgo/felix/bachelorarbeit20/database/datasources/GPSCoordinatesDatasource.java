package com.cyberlandgo.felix.bachelorarbeit20.database.datasources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.cyberlandgo.felix.bachelorarbeit20.database.MySQLiteHelper;
import com.cyberlandgo.felix.bachelorarbeit20.database.models.Station;
import com.cyberlandgo.felix.bachelorarbeit20.database.models.StationGPSCoordinates;

import java.util.ArrayList;

/**
 * Created by Felix on 30.08.2016.
 */
public class GPSCoordinatesDatasource
{
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;

    private String[] allColumns = {
            MySQLiteHelper.COLUMN_ID3,
            MySQLiteHelper.COLUMN_GPS_STATION,
            MySQLiteHelper.COLUMN_LATITUDE,
            MySQLiteHelper.COLUMN_LONGITUDE};

    public GPSCoordinatesDatasource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    public void open() throws SQLException
    {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public StationGPSCoordinates createStationGPSCoordinates(String station_name, int latitude, int longitude)
    {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_GPS_STATION, station_name);
        values.put(MySQLiteHelper.COLUMN_LATITUDE, latitude);
        values.put(MySQLiteHelper.COLUMN_LONGITUDE, longitude);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_GPS_COORDINATES, allColumns,
                MySQLiteHelper.COLUMN_GPS_STATION+ " = '" + station_name + "'" +
                        " AND " + MySQLiteHelper.COLUMN_LATITUDE + " = " + latitude +
                        " AND " + MySQLiteHelper.COLUMN_LONGITUDE + " = " + longitude,
                null, null, null, null);

        StationGPSCoordinates newStationGPSCoordinates = cursorToStationGPSCoordinates(cursor);
    // check if product already exists
        if (cursor.getCount() == 0)
        {

            long insertId = database.insert(MySQLiteHelper.TABLE_GPS_COORDINATES, null, values);
            newStationGPSCoordinates.setId(insertId);
        }
        return newStationGPSCoordinates;
    }

    public ArrayList<StationGPSCoordinates> getAllStationGPSCoordinates() {
        ArrayList<StationGPSCoordinates> stations = new ArrayList<StationGPSCoordinates>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_GPS_COORDINATES, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            StationGPSCoordinates station = cursorToStationGPSCoordinates(cursor);
            stations.add(station);
            cursor.moveToNext();
        }
        cursor.close();
        return stations;
    }


    private StationGPSCoordinates cursorToStationGPSCoordinates(Cursor cursor)
    {
        StationGPSCoordinates station = new StationGPSCoordinates();
        station.setId(cursor.getInt(0));
        station.setStation(cursor.getString(1));
        station.setLatitude(cursor.getString(2));
        station.setLongitude(cursor.getString(3));
        return station;
    }
}
