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

import com.cyberlandgo.felix.bachelorarbeit20.R;

public class FragmentSubsections extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener
{
    //View Objekt, das das Layout des Fragments enthält
    View view;

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
        view = inflater.inflate(R.layout.fragment_overview, container, false);

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
}
