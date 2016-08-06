package com.cyberlandgo.felix.bachelorarbeit20.application;

import android.app.Application;

/**
 * Created by Felix on 06.08.2016.
 */
public class BillingSystemApplication extends Application
{
    Preferences preferences;


    @Override
    public void onCreate()
    {
        super.onCreate();

        //Ezeugen des Preferences-Objekt und Zuweisung der Application als dessen Kontext
        preferences = new Preferences(this);
    }
}
