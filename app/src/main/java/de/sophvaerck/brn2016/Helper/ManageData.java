package de.sophvaerck.brn2016.Helper;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;

import de.sophvaerck.brn2016.R;

/**
 * Created by Jan on 08.06.2016.
 */
public class ManageData {
    public static HashMap<String, Location> getLocations() {
        HashMap<String, Location> javaLocations = new HashMap<>();

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

                    javaLocation.useremail = jsonLocation.getString("useremail");
                    javaLocation.id = jsonLocation.getString("id");
                    javaLocation.name = jsonLocation.getString("name");
                    javaLocation.address = jsonLocation.getString("address");
                    javaLocation.lat = jsonLocation.getDouble("lat");
                    javaLocation.lng = jsonLocation.getDouble("lng");
                    javaLocation.text = jsonLocation.getString("text");
                    javaLocation.url = jsonLocation.getString("url");
                    javaLocation.image = jsonLocation.getString("image");
                    javaLocation.visible = jsonLocation.getString("visible") == "1" ? true : false;
                    javaLocation.tags = jsonLocation.getString("tags");

                    javaLocation.categories = new int[jsonLocation.getJSONArray("categories").length()];
                    for (int i = 0; i < javaLocation.categories.length; i++) {
                        javaLocation.categories[i] = jsonLocation.getJSONArray("categories").getInt(i);
                    }

                    javaLocation.festivals = new String[jsonLocation.getJSONArray("festivals").length()];
                    for (int i = 0; i < javaLocation.festivals.length; i++) {
                        javaLocation.festivals[i] = jsonLocation.getJSONArray("festivals").getString(i);
                    }

                    javaLocations.put(javaLocation.id, javaLocation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return javaLocations;
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
