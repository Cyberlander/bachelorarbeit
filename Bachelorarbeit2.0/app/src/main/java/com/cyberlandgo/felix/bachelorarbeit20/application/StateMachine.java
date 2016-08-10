package com.cyberlandgo.felix.bachelorarbeit20.application;

import java.util.HashMap;

/**
 * Created by Felix on 03.07.2016.
 */
public class StateMachine
{
    public static final int STATUS_NOT_RUNNING = 0;

    public static final int STATUS_START_REGION_TRAIN = 1;

    public static final int STATUS_BETWEEN_REGIONS_TRAIN = 2;

    public static final int STATUS_END_REGION_TRAIN = 3;

    public static final int STATUS_START_REGION_BUS = 4;

    public static final int STATUS_END_REGION_BUS = 5;

    public static HashMap<Integer,String> getStatusNumberStatusNameMap()
    {
        HashMap<Integer,String> map = new HashMap<>();
        map.put(0,"Not running");
        map.put(1,"Start Region Train");
        map.put(2,"Between Regions Train");
        map.put(3,"End Region Train");
        map.put(4,"Start Region Bus");
        map.put(5,"End Region Bus");

        return map;
    }

    public static String getStatusNameForStatusInteger(int status)
    {
       return  getStatusNumberStatusNameMap().get(status);
    }
}
