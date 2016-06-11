package de.sophvaerck.brn2016.Helper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.sophvaerck.brn2016.R;

/**
 * Created by Jan on 11.06.2016.
 */
public class EventArrayAdapter extends ArrayAdapter<Event> {
    private final List<Event> values;
    private final Context context;
    private boolean showLocationInfo = true;

    public EventArrayAdapter(Context context, List<Event> values) {
        this(context, values, true);
    }

    public EventArrayAdapter(Context context, List<Event> values, boolean showLocationInfo) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
        this.showLocationInfo = showLocationInfo;
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

        // text fields
        textTitle.setText(values.get(position).title);

        String text = values.get(position).text;
        if(text.length() == 0) {
            textText.setVisibility(View.GONE);
        } else {
            textText.setText(values.get(position).text);
            textText.setVisibility(View.VISIBLE);
        }

        String url = values.get(position).url;
        if(url.length() == 0 || url.contains("brn-schwafelrunde.de")) {
            textUrl.setVisibility(View.GONE);
        } else {
            textUrl.setText(url);
            textUrl.setVisibility(View.VISIBLE);
        }

        // date
        SimpleDateFormat time = new SimpleDateFormat("HH:mm 'Uhr'", Locale.GERMANY);
        SimpleDateFormat weekDay = new SimpleDateFormat("EEEE", Locale.GERMANY);
        Date start = values.get(position).dateStart;
        Date end = values.get(position).dateEnd;

        String date = weekDay.format(start) + ", " + time.format(start) + " - ";
        if(! weekDay.format(start).equals(weekDay.format(end))) {
            date += weekDay.format(end) + ", ";
        }
        date += time.format(end);

        textDate.setText(date);

        // location
        if(showLocationInfo) {
            Location l = ManageData.getLocation(values.get(position).locationId);
            if (l != null) {
                textLocation.setText(l.name + " || " + l.address);
                textLocation.setVisibility(View.VISIBLE);
            } else {
                textLocation.setVisibility(View.GONE);
            }
        } else {
            textLocation.setVisibility(View.GONE);
        }

        return rowView;
    }
}
