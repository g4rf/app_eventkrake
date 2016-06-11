package de.sophvaerck.brn2016.Helper;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.osmdroid.util.GeoPoint;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class Helper {
    public static final GeoPoint mapCenter = new GeoPoint(51.0665, 13.7538);
    public static final String FestivalId = "brn2016";
    public static final String FestivalName = "Bunte Republik Neustadt 2016";
    public static final Date FestivalStart = (new GregorianCalendar(2016, 6-1, 17, 0, 0)).getTime();
    public static final Date FestivalEnd = (new GregorianCalendar(2016, 6-1, 20, 8, 0)).getTime();

    public static final int mapZoom = 16;
    public static final int mapMaxZoom = 19;

    public static final float mapRotation = -24.556484f;

    public static final SimpleDateFormat mysqlDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMANY);
    public static final SimpleDateFormat readableTime = new SimpleDateFormat("HH:mm 'Uhr'", Locale.GERMANY);
    public static final SimpleDateFormat readableDate = new SimpleDateFormat("dd.MM.", Locale.GERMANY);
    public static final SimpleDateFormat readableNAFDate = new SimpleDateFormat("E, H:mm 'Uhr'", Locale.GERMANY);
    public static final SimpleDateFormat readableLongDate = new SimpleDateFormat("EEEE, dd.MM., HH:mm 'Uhr'", Locale.GERMANY);

    public static Context context = null;
    public static ProgressBar working = null;

    public static void error(String msg) {
        Log.d("ERROR", msg);
    }

    public static void atWork() {
        if(working != null) working.setVisibility(View.VISIBLE);
    }

    public static void stopWork() {
        if(working != null) working.setVisibility(View.GONE);
    }
}
