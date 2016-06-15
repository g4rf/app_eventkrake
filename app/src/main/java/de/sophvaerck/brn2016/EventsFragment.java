package de.sophvaerck.brn2016;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import de.sophvaerck.brn2016.Helper.Event;
import de.sophvaerck.brn2016.Helper.EventArrayAdapter;
import de.sophvaerck.brn2016.Helper.Helper;
import de.sophvaerck.brn2016.Helper.ManageData;

public class EventsFragment extends Fragment {
    View rootView;
    ListView lvEvents;

    String search;

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
        Helper.eventsFragment = this;

        rootView = inflater.inflate(R.layout.fragment_events, container, false);

        lvEvents = (ListView) rootView.findViewById(R.id.lvEvents);

        lvEvents.setAdapter(new EventArrayAdapter(
                rootView.getContext(), ManageData.getEvents()
        ));

        /*Helper.atWork();

        new AsyncTask<Void, Void, ArrayList<Event>>() {

            @Override
            protected ArrayList<Event> doInBackground(Void... params) {
                return ManageData.getEvents();
            }

            @Override
            protected void onPostExecute(ArrayList<Event> result) {
                super.onPostExecute(result);
                lvEvents.setAdapter(new EventArrayAdapter(
                    rootView.getContext(), result
                ));
                Helper.stopWork();
            }
        }.execute();*/

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fabSearch);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText textSearch = new EditText(getContext());
                textSearch.setInputType(InputType.TYPE_CLASS_TEXT);

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("Veranstaltungen durchsuchen")
                    .setView(textSearch)
                    .setPositiveButton("Suchen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            lvEvents.setAdapter(new EventArrayAdapter(
                                rootView.getContext(),
                                ManageData.getEvents(textSearch.getText().toString()),
                                false
                            ));
                        }
                    })
                    .setNegativeButton("Zurücksetzen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            lvEvents.setAdapter(new EventArrayAdapter(
                                    rootView.getContext(), ManageData.getEvents(), false
                            ));
                            dialog.cancel();
                        }
                    });

                builder.show();
            }
        });

        return rootView;
    }
}
