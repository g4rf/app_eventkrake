package de.sophvaerck.brn2016;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import de.sophvaerck.brn2016.Helper.EventArrayAdapter;
import de.sophvaerck.brn2016.Helper.Location;
import de.sophvaerck.brn2016.Helper.LocationArrayAdapter;
import de.sophvaerck.brn2016.Helper.ManageData;

public class LocationsFragment extends Fragment {
    Spinner lvLocations;
    ListView lvEvents;

    public LocationsFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putInt("curChoice", mCurCheckPosition);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_locations, container, false);

        lvLocations = (Spinner) rootView.findViewById(R.id.lvLocations);
        lvEvents = (ListView) rootView.findViewById(R.id.lvEvents);

        lvLocations.setAdapter(new LocationArrayAdapter(
                rootView.getContext(), ManageData.getLocations()
        ));
        lvLocations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final Location item = (Location) parent.getItemAtPosition(position);

                lvEvents.setAdapter(new EventArrayAdapter(
                        rootView.getContext(), ManageData.getEvents(item), false
                ));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //do nothing
            }
        });

        /*lvLocations = (ListView) rootView.findViewById(R.id.lvLocations);
        lvEvents = (ListView) rootView.findViewById(R.id.lvEvents);

        lvLocations.setAdapter(new LocationArrayAdapter(
                rootView.getContext(), ManageData.getLocations()
        ));
        lvLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final Location item = (Location) parent.getItemAtPosition(position);
                ((TextView) rootView.findViewById(R.id.lblEvents)).setText(
                        item.name + " || " + item.address);

                lvEvents.setAdapter(new EventArrayAdapter(
                        rootView.getContext(), ManageData.getEvents(item), false
                ));
            }
        });*/

        return rootView;
    }
}
