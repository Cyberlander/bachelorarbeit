package com.cyberlandgo.felix.bachelorarbeit20.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cyberlandgo.felix.bachelorarbeit20.application.Values;
import com.cyberlandgo.felix.bachelorarbeit20.database.models.Station;
import com.cyberlandgo.felix.bachelorarbeit20.database.models.StationGPSCoordinates;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 02.05.2016.
 */
public class MySQLiteHelper extends SQLiteOpenHelper
{
        //Werte für die Stationen-Tabelle
        public static final String TABLE_STATIONS = "stationTable1";
        public static final String COLUMN_ID= "id";
        public static final String COLUMN_STATION_NAME = "station";
        public static final String COLUMN_MAJOR= "major";
        public static final String COLUMN_MINOR= "minor";

        //Werte für die Teilstrecken
        public static final String TABLE_SUBSECTIONS = "subsectionTable";
        public static final String COLUMN_ID2 = "id2";

        public static final String COLUMN_LINE = "line";
        public static final String COLUMN_FROM = "von"; //from ist ein SQLite-Keyword
        public static final String COLUMN_TO = "zu";


        //Werte für GPS-Koordinaten
        public static final String TABLE_GPS_COORDINATES = "gpsCoordinatesTable";
        public static final String COLUMN_ID3 = "id3";

        public static final String COLUMN_GPS_STATION = "gps_station";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";


        public static final String DATABASE_NAME= "stations.db";
        public static final int DATABASE_VERSION= 1;
        private SQLiteDatabase dbase;


        //Database creation statement
        private static final String DATABASE_CREATE = "create table " + TABLE_STATIONS+
                "("+COLUMN_ID+ " integer primary key autoincrement,"
                + COLUMN_STATION_NAME + " text not null,"
                + COLUMN_MAJOR + " text not null,"
                + COLUMN_MINOR + " text not null);";

        //Subsection-Table creation statement
        private static final String SUBSECTION_CREATE_STATEMENT = "create table " + TABLE_SUBSECTIONS
                + "(" + COLUMN_ID2 + " integer primary key autoincrement,"
                + COLUMN_LINE + " text not null,"
                + COLUMN_FROM + " text not null,"
                + COLUMN_TO + " text not null);";


        //GPSCoordinates-Table creation statement
        private static final String GPSCOORDINATES_CREATE_STATEMENT = "create table " + TABLE_GPS_COORDINATES
                + "(" + COLUMN_ID3 + " integer primary key autoincrement,"
                + COLUMN_GPS_STATION + " text not null,"
                + COLUMN_LATITUDE + " text not null,"
                + COLUMN_LONGITUDE + " text not null);";

        public MySQLiteHelper(Context context)
        {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database)
        {
                dbase = database;

                database.execSQL(DATABASE_CREATE);
                database.execSQL(SUBSECTION_CREATE_STATEMENT);
                database.execSQL(GPSCOORDINATES_CREATE_STATEMENT);

                addStations();
                addStationGPSCoordinates();

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATIONS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_SUBSECTIONS);
            db.execSQL("DROP TABLE IF EXISTS " + GPSCOORDINATES_CREATE_STATEMENT);
        }


        private void addStations()
        {



                //esti008
                Station station42 = new Station("Berliner Tor", "18475","24286");
                this.addStation(station42);




                //Erstellen der U2 Stationen-Objekte
                Station[] stationen_u2 = new Station[25];
                stationen_u2[0] = new Station(Values.STATION_U2_NIENDORF_NORD, Values.MAJOR_ID_TRAIN,"20000");
                stationen_u2[1] = new Station(Values.STATION_U2_SCHIPPELSWEG, Values.MAJOR_ID_TRAIN,"20001");
                stationen_u2[2] = new Station(Values.STATION_U2_JOACHIM_MAEHL_STRASSE, Values.MAJOR_ID_TRAIN,"20002");
                stationen_u2[3] = new Station(Values.STATION_U2_NIENDORF_MARKT, Values.MAJOR_ID_TRAIN,"20003");
                stationen_u2[4] = new Station(Values.STATION_U2_HAGENDEEL, Values.MAJOR_ID_TRAIN,"20004");
                stationen_u2[5] = new Station(Values.STATION_U2_HAGENBECKS_TIERPARK, Values.MAJOR_ID_TRAIN,"20005");
                stationen_u2[6] = new Station(Values.STATION_U2_LUTTHEROTHSTRASSE, Values.MAJOR_ID_TRAIN,"20006");
                stationen_u2[7] = new Station(Values.STATION_U2_OSTERSTRASSE, Values.MAJOR_ID_TRAIN,"20007");
                stationen_u2[8] = new Station(Values.STATION_U2_EMILIENSTRASSE, Values.MAJOR_ID_TRAIN,"20008");
                stationen_u2[9] = new Station(Values.STATION_U2_CHRISTUSKIRCHE, Values.MAJOR_ID_TRAIN,"20009");
                stationen_u2[10] = new Station(Values.STATION_U2_SCHLUMP, Values.MAJOR_ID_TRAIN,"20010");
                stationen_u2[11] = new Station(Values.STATION_U2_MESSEHALLEN, Values.MAJOR_ID_TRAIN,"20011");
                stationen_u2[12] = new Station(Values.STATION_U2_GAENSEMARKT, Values.MAJOR_ID_TRAIN,"20012");
                stationen_u2[13] = new Station(Values.STATION_U2_JUNGFERNSTIEG, Values.MAJOR_ID_TRAIN,"20013");
                stationen_u2[14] = new Station(Values.STATION_U2_HAUPTBAHNHOF_NORD, Values.MAJOR_ID_TRAIN,"20014");
                stationen_u2[15] = new Station(Values.STATION_U2_BERLINER_TOR, Values.MAJOR_ID_TRAIN,"20015");
                stationen_u2[16] = new Station(Values.STATION_U2_BURGSTRASSE, Values.MAJOR_ID_TRAIN,"20016");
                stationen_u2[17] = new Station(Values.STATION_U2_HAMMER_KIRCHE, Values.MAJOR_ID_TRAIN,"20017");
                stationen_u2[18] = new Station(Values.STATION_U2_RAUES_HAUS, Values.MAJOR_ID_TRAIN,"20018");
                stationen_u2[19] = new Station(Values.STATION_U2_HORNER_RENNBAHN, Values.MAJOR_ID_TRAIN,"20019");
                stationen_u2[20] = new Station(Values.STATION_U2_LEGIENSTRASSE, Values.MAJOR_ID_TRAIN,"20020");
                stationen_u2[21] = new Station(Values.STATION_U2_BILLSTEDT, Values.MAJOR_ID_TRAIN,"20021");
                stationen_u2[22] = new Station(Values.STATION_U2_MERKENSTRASSE, Values.MAJOR_ID_TRAIN,"20222");
                stationen_u2[23] = new Station(Values.STATION_U2_STEINFURTER_ALLEE, Values.MAJOR_ID_TRAIN,"20023");
                stationen_u2[24] = new Station(Values.STATION_U2_MUEMMELMANNSBERG, Values.MAJOR_ID_TRAIN,"20024");

                //Erstellen der 281er Bus-Stationen-Objekte
                Station[] stationen_bus_281 = new Station[3];
                stationen_bus_281[0] = new Station(Values.STATION_BUS_281_HAGENBECKS_TIERPARK, Values.MAJOR_ID_BUS,"28100");
                stationen_bus_281[1] = new Station(Values.STATION_BUS_281_RATHAUS_STELLINGEN, Values.MAJOR_ID_BUS,"28101");
                stationen_bus_281[2] = new Station(Values.STATION_BUS_281_INFORMATIKUM, Values.MAJOR_ID_BUS,"28102");



                for (int i=0;i<stationen_u2.length;i++)
                {
                        if (stationen_u2[i]!=null) this.addStation(stationen_u2[i]);
                }

                for (int i=0;i<stationen_bus_281.length;i++)
                {
                        if (stationen_bus_281[i]!=null) this.addStation(stationen_bus_281[i]);
                }


        }


        private void addStation(Station station)
        {
                ContentValues values = new ContentValues();
                values.put(COLUMN_STATION_NAME, station.getStationName());
                values.put(COLUMN_MAJOR, station.getMajor());
                values.put(COLUMN_MINOR, station.getMinor());
                dbase.insert(TABLE_STATIONS,null,values);
        }


        public void addStationGPSCoordinates()
        {
                //Erstellen der U2 GPSCoordinates-Stationen-Objekte
                StationGPSCoordinates[] gpscoordinates_stationen_u2 = new StationGPSCoordinates[25];
                gpscoordinates_stationen_u2[0] = new StationGPSCoordinates(Values.STATION_U2_NIENDORF_NORD, "53.64084966","9.95021009");
                gpscoordinates_stationen_u2[1] = new StationGPSCoordinates(Values.STATION_U2_SCHIPPELSWEG, "53.6355879","9.95249105");
                gpscoordinates_stationen_u2[2] = new StationGPSCoordinates(Values.STATION_U2_JOACHIM_MAEHL_STRASSE, "53.62946276","9.94971657");
                gpscoordinates_stationen_u2[3] = new StationGPSCoordinates(Values.STATION_U2_NIENDORF_MARKT, "53.61939648","9.95145464");
                gpscoordinates_stationen_u2[4] = new StationGPSCoordinates(Values.STATION_U2_HAGENDEEL, "53.60383292","9.94523835");
                gpscoordinates_stationen_u2[5] = new StationGPSCoordinates(Values.STATION_U2_HAGENBECKS_TIERPARK, "53.59249462","9.94421804");
                gpscoordinates_stationen_u2[6] = new StationGPSCoordinates(Values.STATION_U2_LUTTHEROTHSTRASSE, "53.58200678","9.94774139");
                gpscoordinates_stationen_u2[7] = new StationGPSCoordinates(Values.STATION_U2_OSTERSTRASSE, "53.57619494","9.95197606");
                gpscoordinates_stationen_u2[8] = new StationGPSCoordinates(Values.STATION_U2_EMILIENSTRASSE, "53.57165014","9.95279145");
                gpscoordinates_stationen_u2[9] = new StationGPSCoordinates(Values.STATION_U2_CHRISTUSKIRCHE, "53.56963049","9.9623723");
                gpscoordinates_stationen_u2[10] = new StationGPSCoordinates(Values.STATION_U2_SCHLUMP, "53.56760693","9.97017431");
                gpscoordinates_stationen_u2[11] = new StationGPSCoordinates(Values.STATION_U2_MESSEHALLEN, "53.55785984","9.97677469");
                gpscoordinates_stationen_u2[12] = new StationGPSCoordinates(Values.STATION_U2_GAENSEMARKT, "53.55571845","9.98752499");
                gpscoordinates_stationen_u2[13] = new StationGPSCoordinates(Values.STATION_U2_JUNGFERNSTIEG, "53.55272286","9.99492788");
                gpscoordinates_stationen_u2[14] = new StationGPSCoordinates(Values.STATION_U2_HAUPTBAHNHOF_NORD, "53.55333474","10.00527048");
                gpscoordinates_stationen_u2[15] = new StationGPSCoordinates(Values.STATION_U2_BERLINER_TOR, "53.55299183","10.02447081");
                gpscoordinates_stationen_u2[16] = new StationGPSCoordinates(Values.STATION_U2_BURGSTRASSE, "53.55580003","10.04191911");
                gpscoordinates_stationen_u2[17] = new StationGPSCoordinates(Values.STATION_U2_HAMMER_KIRCHE, "53.55556102","10.05558228");
                gpscoordinates_stationen_u2[18] = new StationGPSCoordinates(Values.STATION_U2_RAUES_HAUS, "53.55421303","10.06616092");
                gpscoordinates_stationen_u2[19] = new StationGPSCoordinates(Values.STATION_U2_HORNER_RENNBAHN, "53.55383316","10.08384418");
                gpscoordinates_stationen_u2[20] = new StationGPSCoordinates(Values.STATION_U2_LEGIENSTRASSE, "53.54726652","10.09560728");
                gpscoordinates_stationen_u2[21] = new StationGPSCoordinates(Values.STATION_U2_BILLSTEDT, "53.54225576","10.10743046");
                gpscoordinates_stationen_u2[22] = new StationGPSCoordinates(Values.STATION_U2_MERKENSTRASSE, "53.53864458","10.1255064");
                gpscoordinates_stationen_u2[23] = new StationGPSCoordinates(Values.STATION_U2_STEINFURTER_ALLEE,"53.54093986","10.13795185");
                gpscoordinates_stationen_u2[24] = new StationGPSCoordinates(Values.STATION_U2_MUEMMELMANNSBERG, "53.52816507","10.14993381");

                for (int i=0;i<gpscoordinates_stationen_u2.length;i++)
                {
                        if (gpscoordinates_stationen_u2[i]!=null) this.addGPSCoordinate(gpscoordinates_stationen_u2[i]);
                }
        }



        public void addGPSCoordinate(StationGPSCoordinates stationGPSCoordinates)
        {
                ContentValues values = new ContentValues();
                values.put(COLUMN_GPS_STATION, stationGPSCoordinates.getStation());
                values.put(COLUMN_LATITUDE, stationGPSCoordinates.getLatitude());
                values.put(COLUMN_LONGITUDE, stationGPSCoordinates.getLongitude());
                dbase.insert(TABLE_GPS_COORDINATES,null,values);
        }

}