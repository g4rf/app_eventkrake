package de.sophvaerck.eventkrake.Helper;

import java.util.Date;

/**
 * Created by Jan on 10.06.2016.
 */
public class Event implements Comparable<Event> {
    public String userEmail;
    public String id;
    public String locationId;
    public Date dateStart;
    public Date dateEnd;
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
