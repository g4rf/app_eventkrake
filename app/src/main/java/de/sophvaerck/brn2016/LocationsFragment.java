package de.sophvaerck.brn2016;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import de.sophvaerck.brn2016.Helper.Helper;
import de.sophvaerck.brn2016.Helper.Location;
import de.sophvaerck.brn2016.Helper.LocationArrayAdapter;
import de.sophvaerck.brn2016.Helper.ManageData;

public class LocationsFragment extends Fragment {
    ListView lvLocations;
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
        View rootView = inflater.inflate(R.layout.fragment_locations, container, false);

        lvLocations = (ListView) rootView.findViewById(R.id.lvLocations);

        lvLocations.setAdapter(new LocationArrayAdapter(
                rootView.getContext(), ManageData.getLocations()
        ));
        lvLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                final Location item = (Location) parent.getItemAtPosition(position);

                // TODO: show events

            }
        });

        return rootView;
    }
}
