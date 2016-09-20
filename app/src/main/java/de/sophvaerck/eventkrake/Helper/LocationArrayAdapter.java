package de.sophvaerck.eventkrake.Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.sophvaerck.eventkrake.R;

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
    public int getPosition(Location l) {
        for(int i = 0; i < values.size(); i++) {
            if(l.id.equals(values.get(i).id)) return i;
        }
        return -1;
    }

    public int getPosition(String locationId) {
        for(int i = 0; i < values.size(); i++) {
            if(locationId.equals(values.get(i).id)) return i;
        }
        return -1;
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
