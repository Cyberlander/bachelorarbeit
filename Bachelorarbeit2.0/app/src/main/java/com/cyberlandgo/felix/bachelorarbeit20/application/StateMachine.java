package com.cyberlandgo.felix.bachelorarbeit20.application;

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
}
