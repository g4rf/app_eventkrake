package de.sophvaerck.brn2016.Helper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import org.osmdroid.util.GeoPoint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.sophvaerck.brn2016.EventsFragment;
import de.sophvaerck.brn2016.LocationsFragment;
import de.sophvaerck.brn2016.MainActivity;
import de.sophvaerck.brn2016.MapFragment;
import de.sophvaerck.brn2016.R;

public class Helper {
    public static final GeoPoint mapCenter = new GeoPoint(51.0665, 13.7538);
    public static final String FestivalId = "brn2016";
    public static final String FestivalName = "Bunte Republik Neustadt 2016";
    public static final Date FestivalStart = (new GregorianCalendar(2016, 6-1, 17, 8, 0)).getTime();
    public static final Date FestivalEnd = (new GregorianCalendar(2016, 6-1, 20, 8, 0)).getTime();

    public static final int mapZoom = 16;
    public static final int mapMaxZoom = 19;
    public static final int mapMinZoom = 14;

    public static final float mapRotation = -24.556484f;

    public static final SimpleDateFormat mysqlDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.GERMANY);
    public static final SimpleDateFormat readableTime = new SimpleDateFormat("HH:mm 'Uhr'", Locale.GERMANY);
    public static final SimpleDateFormat readableDate = new SimpleDateFormat("dd.MM.", Locale.GERMANY);
    public static final SimpleDateFormat readableNAFDate = new SimpleDateFormat("E, H:mm 'Uhr'", Locale.GERMANY);
    public static final SimpleDateFormat readableLongDate = new SimpleDateFormat("EEEE, dd.MM., HH:mm 'Uhr'", Locale.GERMANY);

    public static final int PERMISSION_WRITE_MAP = 0;
    public static final int PERMISSION_ACCESS_LOCATION = 1;

    public static Context context = null;
    public static ProgressBar working = null;

    public static MainActivity mainActivity = null;
    public static EventsFragment eventsFragment = null;
    public static LocationsFragment locationsFragment = null;
    public static MapFragment mapFragment = null;

    public static Date startDateForEvents = new Date();

    public static void changeTime(boolean wholeProgram) {
        if(wholeProgram) {
            startDateForEvents = FestivalStart;
            Helper.mainActivity.mainMenu.findItem(R.id.action_time)
                    .setTitle(mainActivity.getString(R.string.action_time_all))
                    .setIcon(ResourcesCompat.getDrawable(
                            mainActivity.getResources(), R.drawable.ic_menu_allevents, null));
        } else {
            startDateForEvents = new Date();
            Helper.mainActivity.mainMenu.findItem(R.id.action_time)
                    .setTitle(mainActivity.getString(R.string.action_time_now))
                    .setIcon(ResourcesCompat.getDrawable(
                            mainActivity.getResources(), R.drawable.ic_menu_newevents, null));;
        }
        // große Eventliste aktualisieren
        eventsFragment.lvEvents.setAdapter(new EventArrayAdapter(
                eventsFragment.rootView.getContext(),
                ManageData.getEvents(Helper.startDateForEvents, Helper.FestivalEnd)
        ));
        // Locationliste aktualisieren
        locationsFragment.lvLocations.setAdapter(new LocationArrayAdapter(
                locationsFragment.rootView.getContext(),
                ManageData.getLocations(Helper.startDateForEvents, Helper.FestivalEnd)
        ));
        /*/ kleine Eventliste aktualisieren
        locationsFragment.lvEvents.setAdapter(new EventArrayAdapter(
                locationsFragment.rootView.getContext(),
                ManageData.getEvents(
                        (Location)locationsFragment.lvLocations.getSelectedItem(),
                        Helper.startDateForEvents,
                        Helper.FestivalEnd),
                false
        ));*/
    }

    public static void message(Context context, String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(msg)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // do nothing
                    }
                });
        builder.show();
    }

    public static void atWork() {
        if(working != null) working.setVisibility(View.VISIBLE);
    }

    public static void stopWork() {
        if(working != null) working.setVisibility(View.GONE);
    }

    public static boolean copyAssetFile(final AssetManager assetManager,
                                        final String assetName,
                                        final String destinationPath) {
        InputStream in;
        OutputStream out;
        final File destinationFile = new File(destinationPath);

        //Log.d(LOG_TAG, String.format(
        //       "Copy %s map archive in assets into %s", assetRelativePath, newfilePath));
        try {
            final File directory = destinationFile.getParentFile();
            if (! directory.exists()) {
                if (directory.mkdirs()) {
                    // Log.d(LOG_TAG, "Directory created: " + directory.getAbsolutePath());
                }
            }
            in = assetManager.open(assetName);
            out = new FileOutputStream(destinationPath);
            // copy file
            final byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            out.flush();
            out.close();
        } catch (final Exception e) {
            return false;
            //Log.e(LOG_TAG, "Exception during copyAssetFile: " + Log.getStackTraceString(e));
        }

        return true;
    }
}
