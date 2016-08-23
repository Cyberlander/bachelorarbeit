package com.cyberlandgo.felix.bachelorarbeit20.Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import com.cyberlandgo.felix.bachelorarbeit20.R;
import com.cyberlandgo.felix.bachelorarbeit20.database.models.Subsection;

import java.util.ArrayList;

/**
 * Created by Felix on 23.07.2016.
 */
public class SubsectionAdapter extends ArrayAdapter<Subsection>
{
    public SubsectionAdapter(Context context, ArrayList<Subsection> subsections)
    {
        super(context,0,subsections);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        Subsection subsection = getItem(position);

        if (convertView==null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_subsection, parent, false);
        }

        TextView item_startstation = (TextView) convertView.findViewById(R.id.item_startstation);
        TextView item_endstation = (TextView) convertView.findViewById(R.id.item_endstation);

        item_startstation.setText(subsection.getFrom());
        item_endstation.setText(subsection.getTo());

        return convertView;

    }
}
