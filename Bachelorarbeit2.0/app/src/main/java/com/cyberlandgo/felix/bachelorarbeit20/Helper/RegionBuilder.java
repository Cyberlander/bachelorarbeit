package com.cyberlandgo.felix.bachelorarbeit20.Helper;

import com.cyberlandgo.felix.bachelorarbeit20.application.Values;
import com.cyberlandgo.felix.bachelorarbeit20.database.models.Station;



import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Felix on 20.05.2016.
 */
public class RegionBuilder
{


    public static List<Region> getRegions(ArrayList<Station> arrayList)
    {
        ArrayList<Station> arrayListStationsFromDatabase = arrayList;


        List<Region> listRegions = new ArrayList<Region>();

        for (int i=0;i<arrayListStationsFromDatabase.size();i++)
        {
            Region regionI = new Region("region"+i,
                    Identifier.parse(Values.GLOBAL_UUID),
                    Identifier.parse(arrayListStationsFromDatabase.get(i).getMajor()),
                    Identifier.parse(arrayListStationsFromDatabase.get(i).getMinor()));
            listRegions.add(regionI);
        }

        return listRegions;
    }
}
