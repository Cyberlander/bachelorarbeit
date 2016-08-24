package com.cyberlandgo.felix.bachelorarbeit20.ui.fragments;

/**
 * Created by Felix on 19.08.2016.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.content.SharedPreferences;
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
        ArrayList<Subsection> list = _subsectionDataSource.getAllSubsections();
        addListView(list);


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

    }

    public void addListView(ArrayList<Subsection> list)
    {

        ArrayList<Subsection> subsection_list = list;

        _adapter = new SubsectionAdapter(getContext(), subsection_list);

        _listview.setAdapter(_adapter);
    }
}
