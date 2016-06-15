package de.sophvaerck.brn2016.Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
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

    static class ViewHolder {
        TextView textName;
        TextView textAddress;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder(); // ViewHolder pattern
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listitem_location, parent, false);

            holder.textName = (TextView) convertView.findViewById(R.id.textName);
            holder.textAddress = (TextView) convertView.findViewById(R.id.textAddress);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textName.setText(values.get(position).name);
        holder.textAddress.setText(values.get(position).address);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = new ViewHolder(); // ViewHolder pattern
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listitem_location, parent, false);

            holder.textName = (TextView) convertView.findViewById(R.id.textName);
            holder.textAddress = (TextView) convertView.findViewById(R.id.textAddress);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textName.setText(values.get(position).name);
        holder.textAddress.setText(values.get(position).address);

        return convertView;
    }
}
