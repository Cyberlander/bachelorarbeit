package com.cyberlandgo.felix.bachelorarbeit20.ui.fragments;

/**
 * Created by Felix on 06.08.2016.
 */
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cyberlandgo.felix.bachelorarbeit20.R;
import com.cyberlandgo.felix.bachelorarbeit20.application.Preferences;
import com.cyberlandgo.felix.bachelorarbeit20.application.StateMachine;
import com.cyberlandgo.felix.bachelorarbeit20.database.datasources.StationDataSource;
import com.cyberlandgo.felix.bachelorarbeit20.database.models.Station;

import java.util.ArrayList;

public class FragmentDebug extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener
{


    //View Objekt, das das Layout des Fragments enthält
    View view;

    //UI-Elemente
    TextView textViewCurrentStatus;
    TextView textViewCurrentStation;
    TextView textViewAmountOfStations;
    TextView textViewGPSState;



    public FragmentDebug() {
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
        view = inflater.inflate(R.layout.fragment_debug, container, false);



        initUIElements();
        updateUI();



        return view;
    }

    public void initUIElements()
    {
        textViewCurrentStatus = (TextView) view.findViewById(R.id.textview_currentStatus);
        textViewCurrentStation = (TextView) view.findViewById(R.id.textview_currentStation);
        textViewAmountOfStations = (TextView) view.findViewById(R.id.textview_currentAmountOfStations);
        textViewGPSState = (TextView) view.findViewById(R.id.textview_GPSState);

        textViewGPSState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                boolean isGPSon = Preferences.getBooleanGPSUsage();
                isGPSon = !isGPSon;
                Preferences.saveBooleanGPSUsage(isGPSon);
            }
        });

    }

    public void updateUI()
    {
        String currentStatusString = StateMachine.getStatusNameForStatusInteger(Preferences.getStatusStateMachine());
        textViewCurrentStatus.setText(currentStatusString);

        //noch keine Startstation gesehen
        if (Preferences.getCurrentStartstation().equals(""))
        {
            textViewCurrentStation.setText("Noch keine Startstation");
        }
        //schon eine Startstation gesehen
        else
        {
            textViewCurrentStation.setText("Station: " + Preferences.getCurrentStartstation());

        }

        textViewAmountOfStations.setText("Besuchte Stationen: " + Preferences.getCurrentAmountOfStations());

        boolean useGPS = Preferences.getBooleanGPSUsage();

        if (!useGPS)
        {
            textViewGPSState.setText("GPS benutzen: nein");
        }
        else if (useGPS)
        {
            textViewGPSState.setText("GPS benutzen: ja");
        }

    }






    /**
     * Callback-Methode, die darauf reagiert, wenn sich die Preferences geändert haben,
     * auf die der Listener lauscht
     * @param sharedPreferences
     * @param key
     */

    @Override
    public void  onSharedPreferenceChanged (SharedPreferences  sharedPreferences, String  key)
    {
        updateUI();
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