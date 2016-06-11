package de.sophvaerck.brn2016.Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import de.sophvaerck.brn2016.R;

/**
 * Created by Jan on 11.06.2016.
 */
public class EventArrayAdapter extends ArrayAdapter<Event> {
    private final List<Event> values;
    private final Context context;

    public EventArrayAdapter(Context context, List<Event> values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.listitem_event, parent, false);

        TextView textDate = (TextView) rowView.findViewById(R.id.textDate);
        TextView textTitle = (TextView) rowView.findViewById(R.id.textTitle);
        TextView textLocation = (TextView) rowView.findViewById(R.id.textLocation);
        TextView textUrl = (TextView) rowView.findViewById(R.id.textUrls);
        TextView textText = (TextView) rowView.findViewById(R.id.textText);

        textTitle.setText(values.get(position).title);


        textLocation.setText(values.get(position));

        return rowView;
    }
}
