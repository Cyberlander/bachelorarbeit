package com.cyberlandgo.felix.bachelorarbeit20.ui.fragments;

/**
 * Created by Felix on 19.08.2016.
 */
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.cyberlandgo.felix.bachelorarbeit20.Helper.SubsectionAdapter;
import com.cyberlandgo.felix.bachelorarbeit20.R;
import com.cyberlandgo.felix.bachelorarbeit20.database.datasources.SubsectionDataSource;
import com.cyberlandgo.felix.bachelorarbeit20.database.models.Subsection;

import java.util.ArrayList;
import java.util.List;

public class FragmentSubsections extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener
{
    //View Objekt, das das Layout des Fragments enthält
    View view;
    SubsectionDataSource _subsectionDataSource;

    SubsectionAdapter _adapter;
    ListView _listview;
    ArrayList<Subsection> _SubsectionObjectlist;
    public FragmentSubsections() {
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
        view = inflater.inflate(R.layout.fragment_subsections, container, false);

        _listview = (ListView) view.findViewById(R.id.id_list_view);

        _subsectionDataSource = new SubsectionDataSource(getContext());
        _subsectionDataSource.open();

        //Liste der Teilstrecken
        _SubsectionObjectlist = _subsectionDataSource.getAllSubsections();
        addListView(_SubsectionObjectlist);

        SharedPreferences sharedPref = getContext().getSharedPreferences("prefs", Context.MODE_PRIVATE);

        //Listener lauscht nun ob sich Preferences ändern, wenn
        //das Fragment sichtbar ist
        sharedPref.registerOnSharedPreferenceChangeListener(this);

        return view;
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
        ArrayList<Subsection> subsection_list = _subsectionDataSource.getAllSubsections();
        _adapter = new SubsectionAdapter(getContext(), subsection_list);
        _listview.setAdapter(_adapter);
    }



    public void addListView(ArrayList<Subsection> list)
    {

        ArrayList<Subsection> subsection_list = list;

        _adapter = new SubsectionAdapter(getContext(), subsection_list);

        _listview.setAdapter(_adapter);
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
