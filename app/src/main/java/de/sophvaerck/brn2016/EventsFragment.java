package de.sophvaerck.brn2016;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import de.sophvaerck.brn2016.Helper.Event;
import de.sophvaerck.brn2016.Helper.EventArrayAdapter;
import de.sophvaerck.brn2016.Helper.Helper;
import de.sophvaerck.brn2016.Helper.ManageData;

public class EventsFragment extends Fragment {
    public View rootView;
    public ListView lvEvents;

    TextView notFound;

    String search = "";

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
        notFound = (TextView)  rootView.findViewById(R.id.textNotFound);

        lvEvents.setAdapter(new EventArrayAdapter(
                rootView.getContext(),
                ManageData.getEvents(Helper.startDateForEvents, Helper.FestivalEnd)
        ));

        FloatingActionButton fab = (FloatingActionButton) rootView.findViewById(R.id.fabSearch);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText textSearch = new EditText(getContext());
                textSearch.setInputType(InputType.TYPE_CLASS_TEXT);
                textSearch.setText(search);
                textSearch.setSelection(0, search.length());
                textSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        textSearch.post(new Runnable() {
                            @Override
                            public void run() {
                                InputMethodManager inputMethodManager =
                                        (InputMethodManager) Helper.mainActivity.getSystemService(
                                                Context.INPUT_METHOD_SERVICE);
                                inputMethodManager.showSoftInput(
                                        textSearch, InputMethodManager.SHOW_IMPLICIT);
                            }
                        });
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                builder.setTitle("Veranstaltungen durchsuchen")
                    .setView(textSearch)
                    .setPositiveButton("Suchen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            search = textSearch.getText().toString();

                            ArrayList<Event> events = ManageData.getEvents(search);

                            notFound.setVisibility(
                                    events.size() == 0 ? View.VISIBLE : View.GONE
                            );

                            lvEvents.setAdapter(new EventArrayAdapter(
                                rootView.getContext(),
                                events,
                                false
                            ));
                        }
                    })
                    .setNegativeButton("Zurücksetzen", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            notFound.setVisibility(View.GONE);
                            lvEvents.setAdapter(new EventArrayAdapter(
                                    rootView.getContext(),
                                    ManageData.getEvents(
                                            Helper.startDateForEvents, Helper.FestivalEnd),
                                    false
                            ));
                            dialog.cancel();
                        }
                    });

                builder.show();

                textSearch.requestFocus();
            }
        });

        //setHasOptionsMenu(true);

        return rootView;
    }
}
