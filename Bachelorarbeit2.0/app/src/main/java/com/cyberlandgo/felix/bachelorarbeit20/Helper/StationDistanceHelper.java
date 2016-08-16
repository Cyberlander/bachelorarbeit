package com.cyberlandgo.felix.bachelorarbeit20.Helper;

import com.cyberlandgo.felix.bachelorarbeit20.application.Values;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Felix on 22.07.2016.
 * Diese Klasse bietet über eine Methode eine Map an, die jeder Station einen
 * Integerwert zuordnet, der von Null beginnt und dann aufsteigt. Nach einer
 * abgeschlossenen Fahrt wird die Differenz dieser beiden Werte ermittelt
 * und so die Menge an passierten Stationen errechnet für den Ubahn-Verkehr
 * Der Aufruf der Methode erfolgt also im Zustand END_STATION_TRAIN
 */
public class StationDistanceHelper
{

    public static Map<String,Integer> getStationNameStationPositionMap()
    {
        Map<String,Integer> station_positon_map = new HashMap<String, Integer>();

        station_positon_map.put(Values.STATION_U2_NIENDORF_NORD,0);
        station_positon_map.put(Values.STATION_U2_SCHIPPELSWEG,1);
        station_positon_map.put(Values.STATION_U2_JOACHIM_MAEHL_STRASSE,2);
        station_positon_map.put(Values.STATION_U2_NIENDORF_MARKT,3);
        station_positon_map.put(Values.STATION_U2_HAGENDEEL,4);
        station_positon_map.put(Values.STATION_U2_HAGENBECKS_TIERPARK,5);
        station_positon_map.put(Values.STATION_U2_LUTTHEROTHSTRASSE,6);
        station_positon_map.put(Values.STATION_U2_OSTERSTRASSE,7);
        station_positon_map.put(Values.STATION_U2_EMILIENSTRASSE,8);
        station_positon_map.put(Values.STATION_U2_CHRISTUSKIRCHE,9);
        station_positon_map.put(Values.STATION_U2_SCHLUMP,10);
        station_positon_map.put(Values.STATION_U2_MESSEHALLEN,11);
        station_positon_map.put(Values.STATION_U2_GAENSEMARKT,12);
        station_positon_map.put(Values.STATION_U2_JUNGFERNSTIEG,13);
        station_positon_map.put(Values.STATION_U2_HAUPTBAHNHOF_NORD,14);
        station_positon_map.put(Values.STATION_U2_BERLINER_TOR,15);
        station_positon_map.put(Values.STATION_U2_BURGSTRASSE,16);
        station_positon_map.put(Values.STATION_U2_HAMMER_KIRCHE,17);
        station_positon_map.put(Values.STATION_U2_RAUES_HAUS,18);
        station_positon_map.put(Values.STATION_U2_HORNER_RENNBAHN,19);
        station_positon_map.put(Values.STATION_U2_LEGIENSTRASSE,20);
        station_positon_map.put(Values.STATION_U2_BILLSTEDT,21);
        station_positon_map.put(Values.STATION_U2_MERKENSTRASSE,22);
        station_positon_map.put(Values.STATION_U2_STEINFURTER_ALLEE,23);
        station_positon_map.put(Values.STATION_U2_MUEMMELMANNSBERG,24);




        return station_positon_map;
    }

    public static int  getPositionForName(String name)
    {
        Map<String,Integer> station_positon_map = getStationNameStationPositionMap();
        return station_positon_map.get(name);
    }
}
