package de.sophvaerck.brn2016.Helper;

import java.util.Calendar;

/**
 * Created by Jan on 10.06.2016.
 */
public class Event implements Comparable<Event> {
    public String useremail;
    public String id;
    public String locationId;
    public Calendar dateStart;
    public Calendar dateEnd;
    public String title;
    public String excerpt;
    public String text;
    public String url;
    public String image;
    public boolean visible;
    public int[] categories;
    public String festival;
    public String tags;

    @Override
    public int compareTo(Event another) {
        return this.dateStart.compareTo(another.dateStart);
    }
}
