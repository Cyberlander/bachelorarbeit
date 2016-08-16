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
import android.widget.Toast;

import com.cyberlandgo.felix.bachelorarbeit20.Helper.StationDistanceHelper;
import com.cyberlandgo.felix.bachelorarbeit20.R;
import com.cyberlandgo.felix.bachelorarbeit20.application.Preferences;
import com.cyberlandgo.felix.bachelorarbeit20.application.StateMachine;
import com.cyberlandgo.felix.bachelorarbeit20.application.Values;
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

        //Preferences.saveBooleanDetailedViewTextfieldStartstation(true);

        initUIElements();
        updateUI();




        return view;
    }

    public void initUIElements()
    {
        textViewStartStation = (TextView) view.findViewById(R.id.textview_startstation);
        textViewTargetStation = (TextView) view.findViewById(R.id.textview_targetstation);
        textViewPayTicket = (TextView) view.findViewById(R.id.textview_payticket);

        textViewStartStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                changeStartStationDetails();
            }
        });

        textViewPayTicket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                payTicketResetPreferences();
            }
        });


    }

    public void updateUI()
    {

        //wenn die App das erste Mal gestartet wurde, sind
        //die Preferences für gewöhnlich noch leer (sie
        //werden erst beim Betreten einer Region gefüllt)
        if (Preferences.getStartStation().equals(""))
        {
            textViewStartStation.setText("Keine Startstation");
        }
        //wenn die Preference für die StartStation nicht leer ist,
        //belege das Textfeld mit dem Namen der Startstation
        else if (!Preferences.getStartStation().equals("") && !Preferences.getBooleanDetailedViewTextfieldStartstation())
        {
            String resultTextStart = Preferences.getStartStation() + "\n Startstation";
            SpannableString ss1=  new SpannableString(resultTextStart);
            ss1.setSpan(new RelativeSizeSpan(1.617f), 0,Preferences.getStartStation().length(), 0);
            textViewStartStation.setText(ss1);
        }
        else if (!Preferences.getStartStation().equals("") && Preferences.getBooleanDetailedViewTextfieldStartstation())
        {
            String station = Preferences.getStartStation();
            String date = Preferences.getStartDate();
            String time = Preferences.getStartTime();

            String resultTextStart = station + "\n" +  date + "\n" + time;

            textViewStartStation.setText(resultTextStart);
        }


        //das Fragment holt sich die aktuellen Werte für die
        //Endstation aus den Preferences und aktualisiert die UI
        if (Preferences.getCurrentTargetStation().equals(""))
        {
            textViewTargetStation.setText("Keine Endstation");
        }
        else if (!Preferences.getCurrentTargetStation().equals(""))
        {
            String resultTextStart = Preferences.getCurrentTargetStation() + "\n Endstation";
            SpannableString ss1=  new SpannableString(resultTextStart);
            ss1.setSpan(new RelativeSizeSpan(1.617f), 0,Preferences.getCurrentTargetStation().length(), 0);
            textViewTargetStation.setText(ss1);
        }


        if (Preferences.getBooleanHasToPayTicket()==true)
        {
            String stringPayTicket = "Ticket bezahlen!";
            String resultTextPayTicket = stringPayTicket + "\n" + "0,00 Euro";
            SpannableString ss1=  new SpannableString(resultTextPayTicket);
            ss1.setSpan(new RelativeSizeSpan(1.617f), 0,stringPayTicket.length(), 0);
            textViewPayTicket.setText(ss1);
        }
        else if (Preferences.getBooleanHasToPayTicket()==false)
        {
            textViewPayTicket.setText("Kein Ticket");
        }


    }


    public void changeStartStationDetails()
    {
        boolean detailAnsicht = Preferences.getBooleanDetailedViewTextfieldStartstation();
        detailAnsicht = !detailAnsicht;
        Preferences.saveBooleanDetailedViewTextfieldStartstation(detailAnsicht);
        updateUI();
    }


    /**
     * Diese Methode setzt alle Preferences in ihren Anfangszustand, d.h. auch
     * der Automat befindet sich wieder im Zustand not running
     */
    //TODO Kommunikation mit dem Server (Logging)
    public void payTicketResetPreferences()
    {
        if (Preferences.getBooleanHasToPayTicket()==true)
        {
            //Zurücksetzen sämtlicher Werte
            Preferences.saveBooleanHasToPayTicket(false);
            Preferences.saveCurrentAmountOfStations(0);
            Preferences.saveCurrentStartstation("");
            Preferences.saveStartStation("");
            Preferences.saveStartDate("");
            Preferences.saveStartTime("");
            Preferences.saveCurrentTargetStation("");
            Preferences.saveStatusStateMachine(StateMachine.STATUS_NOT_RUNNING);
        }
    }



    /**
     * Callback-Methode, die darauf reagiert, wenn sich die Preferences geändert haben,
     * auf die der Listener lauscht
     * @param sharedPreferences
     * @param key
     */

    @Override
    public void  onSharedPreferenceChanged  (SharedPreferences  sharedPreferences, String  key)
    {
        updateUI();
    }






    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPref = getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        //Listener lauscht nun ob sich Preferences ändern, wenn
        //das Fragment sichtbar ist
        sharedPref.registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        SharedPreferences sharedPref = getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);
        sharedPref.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

}