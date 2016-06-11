package de.sophvaerck.brn2016;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import de.sophvaerck.brn2016.Helper.EventArrayAdapter;
import de.sophvaerck.brn2016.Helper.ManageData;

public class EventsFragment extends Fragment {
    ListView lvEvents;

    public EventsFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //outState.putInt("curChoice", mCurCheckPosition);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_events, container, false);

        lvEvents = (ListView) rootView.findViewById(R.id.lvEvents);

        lvEvents.setAdapter(new EventArrayAdapter(
                rootView.getContext(), ManageData.getEvents()
        ));

        return rootView;
    }
}
