package com.cyberlandgo.felix.bachelorarbeit20.application;

import android.content.Context;
import android.content.SharedPreferences;

import com.cyberlandgo.felix.bachelorarbeit20.ui.fragments.FragmentOverview;

/**
 * Created by Felix on 06.08.2016.
 */
public class Preferences
{
    private static final String PREFERENCES_FILE = "prefs";

    private static Preferences INSTANCE;
    private static Context mContext;
    public static final String keyStartStation = "keyStartStation";
    public static final String keySecondStartStation = "keySecondStartStation";
    public static final String keyStatusStateMachine = "keyStatusStateMachine";
    public static final String keyStartDate = "keyStartDate";
    public static final String keyStartTime = "keyStartTime";
    public static final String keyTargetStation = "keyTargetStation";
    public static final String keyCurrentMinorIDTargetStation = "keyCurrentMinorIDTargetStation";
    public static final String keyBooleanHasToPayTicket = "keyBooleanHasToPayTicket";
    public static final String keyBooleanIsBluetoothGuardActive = "keyBooleanIsBluetoothGuardActive";




    public static final String keyCurrentAmountOfStations = "keyCurrentAmountOfStations";



    public static final String keyBooleanDetailedViewTextfieldStartstation = "keyBooleanDetailedViewTextfieldStartstation";
    public static final String keyBooleanDetailedViewTextfieldTargetstation = "keyBooleanDetailedViewTextfieldTargetstation";
    public static final String keysaveCurrentStartstation = "keysaveCurrentStartstation";




    public Preferences(Context context)
    {
        INSTANCE = this;
        mContext = context;


    }


    //Startstation
    public static void saveStartStation(String startstation)
    {
        saveSharedSetting(mContext, keyStartStation, startstation);
    }

    public static String getStartStation()
    {
        return readSharedSetting(mContext, keyStartStation);
    }




    //Startstation 2 usw
    //wenn vom Status end train in between train gewechselt wird,
    //dann ändert sich auch die Startstation für die Berechnung (Amount of Stations)
    public static void saveSecondStartStation(String startstation)
    {
        saveSharedSetting(mContext, keySecondStartStation, startstation);
    }

    public static String getSecondStartStation()
    {
        return readSharedSetting(mContext, keySecondStartStation);
    }




    //Status des Automaten
    public static void saveStatusStateMachine(int status)
    {
        saveSharedSettingInteger(mContext, keyStatusStateMachine, status);
    }

    public static int getStatusStateMachine()
    {
        return readSharedSettingInteger(mContext, keyStatusStateMachine);
    }


    //Datum beim Start
    public static void saveStartDate(String date)
    {
        saveSharedSetting(mContext, keyStartDate, date);
    }

    public static String getStartDate()
    {
        return readSharedSetting(mContext, keyStartDate);
    }


    //Zeit beim Start
    public static void saveStartTime(String date)
    {
        saveSharedSetting(mContext, keyStartTime, date);
    }

    public static String getStartTime()
    {
        return readSharedSetting(mContext, keyStartTime);
    }


    //akutelle Endstation
    public static void saveCurrentTargetStation(String target)
    {
        saveSharedSetting(mContext, keyTargetStation, target);
    }

    public static String getCurrentTargetStation()
    {
        return readSharedSetting(mContext, keyTargetStation);
    }

    //akutelle Minor-ID der Endstation
    public static void saveCurrentMinorIDTargetStation(String target)
    {
        saveSharedSetting(mContext, keyCurrentMinorIDTargetStation, target);
    }

    public static String getCurrentMinorIDTargetStation()
    {
        return readSharedSetting(mContext, keyCurrentMinorIDTargetStation);
    }

    //Menge der Stationen
    public static void saveCurrentAmountOfStations(int amount)
    {
        saveSharedSettingInteger(mContext, keyCurrentAmountOfStations, amount);
    }

    public static int getCurrentAmountOfStations()
    {
        return readSharedSettingInteger(mContext, keyCurrentAmountOfStations);
    }



    //Status ob ein Ticket bezahlt werden muss
    public static void saveBooleanHasToPayTicket(boolean value)
    {
        saveSharedSettingBoolean(mContext, keyBooleanHasToPayTicket, value);
    }

    public static boolean getBooleanHasToPayTicket()
    {
        return readSharedSettingBoolean(mContext, keyBooleanHasToPayTicket);
    }



    //Boolean für Ansicht des Startstation-Textfeldes
    public static void saveBooleanDetailedViewTextfieldStartstation(boolean value)
    {
        saveSharedSettingBoolean(mContext, keyBooleanDetailedViewTextfieldStartstation, value);
    }

    public static boolean getBooleanDetailedViewTextfieldStartstation()
    {
        return readSharedSettingBoolean(mContext, keyBooleanDetailedViewTextfieldStartstation);
    }



    //Boolean für Ansicht des Endstation-Textfeldes
    public static void saveBooleanDetailedViewTextfieldTargetstation(boolean value)
    {
        saveSharedSettingBoolean(mContext, keyBooleanDetailedViewTextfieldTargetstation, value);
    }

    public static boolean getBooleanDetailedViewTextfieldTargetstation()
    {
        return readSharedSettingBoolean(mContext, keyBooleanDetailedViewTextfieldTargetstation);
    }








    //Boolean Bluetooth Guard
    public static void saveBooleanIsBluetoothGuardActive(boolean value)
    {
        saveSharedSettingBoolean(mContext, keyBooleanIsBluetoothGuardActive, value);
    }

    public static boolean getBooleanIsBluetoothGuardActive()
    {
        return readSharedSettingBoolean(mContext, keyBooleanIsBluetoothGuardActive);
    }







    //aktuelle Station für DebugZwecke bzw. zuletzt gesehende Station
    public static void saveCurrentStartstation(String value)
    {
        saveSharedSetting(mContext, keysaveCurrentStartstation, value);
    }

    public static String getCurrentStartstation()
    {
        return readSharedSetting(mContext, keysaveCurrentStartstation);
    }









    //Shared Preferences Operationen für Strings
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


    //Shared Preferences Operationen für Integer
    private static void saveSharedSettingInteger(Context context, String settingName, int settingValue)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(settingName, settingValue);
        editor.apply();
    }



    private static int readSharedSettingInteger(Context context, String settingName)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getInt(settingName, 0);
    }






    //Shared Preferences Operationen für Boolean
    private static void saveSharedSettingBoolean(Context context, String settingName, boolean settingValue)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(settingName, settingValue);
        editor.apply();
    }

    private static boolean readSharedSettingBoolean(Context context, String settingName)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getBoolean(settingName, false);
    }
}
