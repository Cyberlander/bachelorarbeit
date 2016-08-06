package com.cyberlandgo.felix.bachelorarbeit20.database.datasources;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.cyberlandgo.felix.bachelorarbeit20.database.MySQLiteHelper;
import com.cyberlandgo.felix.bachelorarbeit20.database.models.Station;

import java.util.ArrayList;

/**
 * Created by Felix on 02.05.2016.
 */
public class StationDataSource {
    private SQLiteDatabase database;
    private MySQLiteHelper dbHelper;
    private String[] allColumns = {MySQLiteHelper.COLUMN_ID, MySQLiteHelper.COLUMN_STATION_NAME, MySQLiteHelper.COLUMN_MAJOR, MySQLiteHelper.COLUMN_MINOR};

    public StationDataSource(Context context) {
        dbHelper = new MySQLiteHelper(context);
    }

    /**
     * Opens up a database connection and gets a writable database.
     *
     * @throws SQLException
     */
    public void open() throws SQLException
    {
        database = dbHelper.getWritableDatabase();
    }



    /**
     * Closes the database connection.
     */
    public void close() {
        dbHelper.close();
    }


    public Station createStation(String station_name, int major, int minor)
    {
        ContentValues values = new ContentValues();
        values.put(MySQLiteHelper.COLUMN_STATION_NAME, station_name);
        values.put(MySQLiteHelper.COLUMN_MAJOR, major);
        values.put(MySQLiteHelper.COLUMN_MINOR, minor);

        Cursor cursor = database.query(MySQLiteHelper.TABLE_STATIONS, allColumns,
                MySQLiteHelper.COLUMN_STATION_NAME + " = '" + station_name + "'" +
                        " AND " + MySQLiteHelper.COLUMN_MAJOR + " = " + major +
                        " AND " + MySQLiteHelper.COLUMN_MINOR + " = " + minor,
                null, null, null, null);

        Station newStation = cursorToStation(cursor);

        // check if product already exists
        if (cursor.getCount() == 0) {

            long insertId = database.insert(MySQLiteHelper.TABLE_STATIONS, null, values);
            newStation.setId(insertId);
        }
        return newStation;
    }


    public void deleteStation(Station station) {
        long id = station.getId();
        database.delete(MySQLiteHelper.TABLE_STATIONS, MySQLiteHelper.COLUMN_ID + "=" + id, null);
    }


    public ArrayList<Station> getAllStations() {
        ArrayList<Station> stations = new ArrayList<Station>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_STATIONS, allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Station station = cursorToStation(cursor);
            stations.add(station);
            cursor.moveToNext();
        }
        cursor.close();
        return stations;
    }


    private Station cursorToStation(Cursor cursor) {
        Station station = new Station();
        station.setId(cursor.getInt(0));
        station.setStationName(cursor.getString(1));
        station.setMajor(cursor.getString(2));
        station.setMinor(cursor.getString(3));
        return station;
    }
}
