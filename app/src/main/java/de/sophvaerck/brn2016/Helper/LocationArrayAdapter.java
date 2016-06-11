package de.sophvaerck.brn2016.Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import de.sophvaerck.brn2016.R;

/**
 * Created by Jan on 11.06.2016.
 */
public class LocationArrayAdapter extends ArrayAdapter<Location> {
    private final List<Location> values;
    private final Context context;

    public LocationArrayAdapter(Context context, List<Location> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listitem_location, parent, false);

        TextView textName = (TextView) rowView.findViewById(R.id.textName);
        TextView textAddress = (TextView) rowView.findViewById(R.id.textAddress);

        textName.setText(values.get(position).name);
        textAddress.setText(values.get(position).address);

        return rowView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        View dropDownView = inflater.inflate(R.layout.listitem_location, parent, false);

        TextView textName = (TextView) dropDownView.findViewById(R.id.textName);
        TextView textAddress = (TextView) dropDownView.findViewById(R.id.textAddress);

        textName.setText(values.get(position).name);
        textAddress.setText(values.get(position).address);

        return dropDownView;

    }
}
