package com.cyberlandgo.felix.bachelorarbeit20.ui.fragments;

/**
 * Created by Felix on 06.08.2016.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cyberlandgo.felix.bachelorarbeit20.R;
import com.cyberlandgo.felix.bachelorarbeit20.application.Preferences;
import com.cyberlandgo.felix.bachelorarbeit20.database.datasources.StationDataSource;
import com.cyberlandgo.felix.bachelorarbeit20.database.models.Station;

import java.util.ArrayList;

public class FragmentOverview extends Fragment
{
    StationDataSource stationDataSource;

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
        View view = inflater.inflate(R.layout.fragment_overview, container, false);

        stationDataSource = new StationDataSource(getContext());
        stationDataSource.open();
        ArrayList<Station> l = stationDataSource.getAllStations();
        Station station1 = l.get(0);

        Preferences.saveBooleanDetailedViewTextfieldStartstation(true);


        //Toast.makeText(getContext(), "" + station1.getStationName(), Toast.LENGTH_SHORT).show();
        Toast.makeText(getContext(), "" + Preferences.getBooleanDetailedViewTextfieldStartstation(), Toast.LENGTH_SHORT).show();

        return view;
    }

}