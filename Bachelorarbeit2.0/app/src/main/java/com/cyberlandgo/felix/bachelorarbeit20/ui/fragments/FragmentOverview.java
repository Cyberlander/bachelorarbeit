package com.cyberlandgo.felix.bachelorarbeit20.ui.fragments;

/**
 * Created by Felix on 06.08.2016.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.cyberlandgo.felix.bachelorarbeit20.R;
import com.cyberlandgo.felix.bachelorarbeit20.application.Preferences;
import com.cyberlandgo.felix.bachelorarbeit20.database.datasources.StationDataSource;
import com.cyberlandgo.felix.bachelorarbeit20.database.models.Station;

import java.util.ArrayList;

public class FragmentOverview extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener
{
    StationDataSource stationDataSource;

    //View Objekt, das das Layout des Fragments enthält
    View view;

    //UI-Elemente
    TextView textViewStartStation;
    TextView textViewTargetStation;
    TextView textViewPayTicket;

    public FragmentOverview() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_overview, container, false);

        stationDataSource = new StationDataSource(getContext());
        stationDataSource.open();
        ArrayList<Station> l = stationDataSource.getAllStations();
        Station station1 = l.get(0);

        Preferences.saveBooleanDetailedViewTextfieldStartstation(true);

        initUIElements();
        updateUI();

        //Toast.makeText(getContext(), "" + station1.getStationName(), Toast.LENGTH_SHORT).show();
        //Toast.makeText(getContext(), "" + Preferences.getBooleanDetailedViewTextfieldStartstation(), Toast.LENGTH_SHORT).show();

        return view;
    }

    public void initUIElements()
    {
        textViewStartStation = (TextView) view.findViewById(R.id.textview_startstation);
        textViewTargetStation = (TextView) view.findViewById(R.id.textview_targetstation);
        textViewPayTicket = (TextView) view.findViewById(R.id.textview_payticket);


    }

    public void updateUI()
    {
        if (Preferences.getStartStation().equals(""))
        {
            textViewStartStation.setText("Keine Startstation");
        }
        else
        {
            textViewStartStation.setText(Preferences.getStartStation());
        }

    }



    @Override
    public void  onSharedPreferenceChanged  (SharedPreferences  sharedPreferences, String  key)
    {
        textViewStartStation.setText(Preferences.getStartStation());
        Log.e("yeah","Startstation wurde gefunden");
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPref = getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        sharedPref.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        SharedPreferences sharedPref = getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        sharedPref.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

}