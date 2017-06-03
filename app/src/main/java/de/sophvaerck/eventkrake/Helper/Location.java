package de.sophvaerck.eventkrake.Helper;

import java.util.ArrayList;

/**
 * Created by Jan on 10.06.2016.
 */
public class Location implements Comparable<Location> {
    public String userEmail;
    public String id;
    public String name;
    public String address;
    public double lat;
    public double lng;
    public String text;
    public String url;
    public String image;
    public boolean visible;
    public ArrayList<String> categories = new ArrayList();;
    public ArrayList<String> festivals = new ArrayList();;
    public String tags;

    @Override
    public int compareTo(Location another) {
        return this.name.compareToIgnoreCase(another.name);
    }
}
