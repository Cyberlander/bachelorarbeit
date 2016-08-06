package com.cyberlandgo.felix.bachelorarbeit20.application;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Felix on 06.08.2016.
 */
public class Preferences
{
    private static final String PREFERENCES_FILE = "prefs";

    private static Preferences INSTANCE;
    private static Context mContext;
    public static final String keyStartStation = "keyStartStation";


    public Preferences(Context context)
    {
        INSTANCE = this;
        mContext = context;
    }


    //EvolutionCalculator
    public static void saveStartStation(String startstation)
    {
        saveSharedSetting(mContext, keyStartStation, startstation);
    }

    public static String getStartStation()
    {
        return readSharedSetting(mContext, keyStartStation);
    }















    private static void saveSharedSetting(Context context, String settingName, String settingValue)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }



    private static String readSharedSetting(Context context, String settingName)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, "");
    }
}
