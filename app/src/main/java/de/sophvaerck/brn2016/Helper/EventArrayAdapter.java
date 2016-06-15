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

    static class ViewHolder {
        TextView textDate;
        TextView textTitle;
        TextView textLocation;
        TextView textUrl;
        TextView textText;
        int position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // ViewHolder pattern
        ViewHolder holder = new ViewHolder();
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listitem_event, null);

            holder.textDate = (TextView) convertView.findViewById(R.id.textDate);
            holder.textTitle = (TextView) convertView.findViewById(R.id.textTitle);
            holder.textLocation = (TextView) convertView.findViewById(R.id.textLocation);
            holder.textUrl = (TextView) convertView.findViewById(R.id.textUrls);
            holder.textText = (TextView) convertView.findViewById(R.id.textText);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Event e = getItem(position);

        // text fields
        holder.textTitle.setText(e.title);

        if(e.text.length() == 0) {
            holder.textText.setVisibility(View.GONE);
        } else {
            holder.textText.setText(e.text);
            holder.textText.setVisibility(View.VISIBLE);
        }

        if(e.url.length() == 0 || e.url.contains("brn-schwafelrunde.de")) {
            holder.textUrl.setVisibility(View.GONE);
        } else {
            holder.textUrl.setText(e.url);
            holder.textUrl.setVisibility(View.VISIBLE);
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

        holder.textDate.setText(date);

        // location
        if(showLocationInfo) {
            Location l = ManageData.getLocation(values.get(position).locationId);
            if (l != null) {
                holder.textLocation.setText(l.name + " || " + l.address);
                holder.textLocation.setVisibility(View.VISIBLE);
            } else {
                holder.textLocation.setVisibility(View.GONE);
            }
        } else {
            holder.textLocation.setVisibility(View.GONE);
        }

        return convertView;
    }
}
