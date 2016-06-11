package de.sophvaerck.brn2016.Helper;

import android.widget.ArrayAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import de.sophvaerck.brn2016.R;

/**
 * Created by Jan on 08.06.2016.
 */
public class ManageData {
    private static ArrayList<Location> locations = new ArrayList<>();
    private static ArrayList<Event> events = new ArrayList<>();

    public static void invalidate() {
        locations.clear();
        events.clear();
    }

    public static Location getLocation(String id) {
        if(locations.size() == 0) getLocations();

        for (Location l: locations) {
            if(l.id.equals(id)) {
                return l;
            }
        }

        return null;
    }

    public static Event getEvent(String id) {
        if(events.size() == 0) getEvents();

        for (Event e: events) {
            if(e.id.equals(id)) {
                return e;
            }
        }

        return null;
    }

    public static ArrayList<Event> getEvents(Location l) {
        if(events.size() == 0) getEvents();

        ArrayList<Event> locationEvents = new ArrayList<>();
        for (Event e: events) {
            if(e.locationId.equals(l.id)) locationEvents.add(e);
        }

        return locationEvents;
    }

    public static ArrayList<Event> getEvents() {
        // cached events
        if(events.size() > 0) return events;

        // get events
        Helper.atWork();

        try {
            JSONObject data = new JSONObject(readData());
            JSONObject jsonEvents = data.getJSONObject("events");

            Iterator<String> iterator = jsonEvents.keys();
            while(iterator.hasNext()) {
                String id = iterator.next();
                try {
                    JSONObject jsonEvent = jsonEvents.getJSONObject(id);
                    Event javaEvent = new Event();

                    javaEvent.userEmail = jsonEvent.getString("useremail");
                    javaEvent.id = jsonEvent.getString("id");
                    javaEvent.locationId = jsonEvent.getString("locationid");
                    javaEvent.title = jsonEvent.getString("title");
                    javaEvent.excerpt = jsonEvent.getString("excerpt");
                    javaEvent.text = jsonEvent.getString("text");
                    javaEvent.url = jsonEvent.getString("url");
                    javaEvent.image = jsonEvent.getString("image");
                    javaEvent.festival = jsonEvent.getString("festival");
                    javaEvent.visible = jsonEvent.getString("visible").equals("true");
                    javaEvent.tags = jsonEvent.getString("tags");

                    javaEvent.dateStart = Helper.mysqlDate.parse(jsonEvent.getString("datetime"));
                    javaEvent.dateEnd = Helper.mysqlDate.parse(jsonEvent.getString("datetime_end"));

                    javaEvent.categories = new int[jsonEvent.getJSONArray("categories").length()];
                    for (int i = 0; i < javaEvent.categories.length; i++) {
                        javaEvent.categories[i] = jsonEvent.getJSONArray("categories").getInt(i);
                    }

                    events.add(javaEvent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // sorting
        Collections.sort(events);

        Helper.stopWork();

        return events;
    }

    public static ArrayList<Location> getLocations() {
        // cached locations
        if(locations.size() > 0) return locations;

        // get locations
        Helper.atWork();

        try {
            JSONObject data = new JSONObject(readData());
            JSONObject jsonLocations = data.getJSONObject("locations");

            Iterator<String> iterator = jsonLocations.keys();
            while(iterator.hasNext()) {
                String id = iterator.next();
                try {
                    if(id.equals("0")) continue; // falls es Events ohne Location gibt
                    JSONObject jsonLocation = jsonLocations.getJSONObject(id);
                    Location javaLocation = new Location();

                    javaLocation.userEmail = jsonLocation.getString("useremail");
                    javaLocation.id = jsonLocation.getString("id");
                    javaLocation.name = jsonLocation.getString("name");
                    javaLocation.address = jsonLocation.getString("address");
                    javaLocation.lat = jsonLocation.getDouble("lat");
                    javaLocation.lng = jsonLocation.getDouble("lng");
                    javaLocation.text = jsonLocation.getString("text");
                    javaLocation.url = jsonLocation.getString("url");
                    javaLocation.image = jsonLocation.getString("image");
                    javaLocation.visible = jsonLocation.getString("visible").equals("true");
                    javaLocation.tags = jsonLocation.getString("tags");

                    javaLocation.categories = new int[jsonLocation.getJSONArray("categories").length()];
                    for (int i = 0; i < javaLocation.categories.length; i++) {
                        javaLocation.categories[i] = jsonLocation.getJSONArray("categories").getInt(i);
                    }

                    javaLocation.festivals = new String[jsonLocation.getJSONArray("festivals").length()];
                    for (int i = 0; i < javaLocation.festivals.length; i++) {
                        javaLocation.festivals[i] = jsonLocation.getJSONArray("festivals").getString(i);
                    }

                    locations.add(javaLocation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // sorting
        Collections.sort(locations);

        Helper.stopWork();

        return locations;
    }

    private static String readData() {
        File file = new File(Helper.context.getFilesDir(), "events.json");

        InputStream is = null;
        if(! file.exists() || ! file.canRead() || file.length() == 0) { // read from resource
            is = Helper.context.getResources().openRawResource(R.raw.events);
        } else { // read from file
            try {
                is = Helper.context.openFileInput("events.json");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (is == null) return "";

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();
    }
}
