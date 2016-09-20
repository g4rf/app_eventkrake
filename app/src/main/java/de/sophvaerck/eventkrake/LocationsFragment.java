package de.sophvaerck.eventkrake;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;

import de.sophvaerck.eventkrake.Helper.EventArrayAdapter;
import de.sophvaerck.eventkrake.Helper.Helper;
import de.sophvaerck.eventkrake.Helper.Location;
import de.sophvaerck.eventkrake.Helper.LocationArrayAdapter;
import de.sophvaerck.eventkrake.Helper.ManageData;

public class LocationsFragment extends Fragment {
    public View rootView;
    public Spinner lvLocations;
    public ListView lvEvents;

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
        Helper.locationsFragment = this;

        rootView = inflater.inflate(R.layout.fragment_locations, container, false);

        lvLocations = (Spinner) rootView.findViewById(R.id.lvLocations);
        lvEvents = (ListView) rootView.findViewById(R.id.lvEvents);

        lvLocations.setAdapter(new LocationArrayAdapter(
                rootView.getContext(),
                ManageData.getLocations(Helper.startDateForEvents, Helper.FestivalEnd)
        ));
        lvLocations.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                final Location item = (Location) parent.getItemAtPosition(position);

                lvEvents.setAdapter(new EventArrayAdapter(
                        rootView.getContext(),
                        ManageData.getEvents(item, Helper.startDateForEvents, Helper.FestivalEnd),
                        false
                ));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        return rootView;
    }
}
