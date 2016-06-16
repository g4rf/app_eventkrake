package de.sophvaerck.brn2016;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
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
import android.widget.TextView;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import de.sophvaerck.brn2016.Helper.Event;
import de.sophvaerck.brn2016.Helper.EventArrayAdapter;
import de.sophvaerck.brn2016.Helper.Helper;
import de.sophvaerck.brn2016.Helper.Location;
import de.sophvaerck.brn2016.Helper.LocationArrayAdapter;
import de.sophvaerck.brn2016.Helper.ManageData;

public class MapFragment extends Fragment implements LocationListener {
    ResourceProxyImpl mResourceProxy;
    MapView map;
    IMapController mapController;
    RotationGestureOverlay mRotationGestureOverlay;
    ItemizedIconOverlay<OverlayItem> mMarkerOverlay;
    OverlayItem lastFocus;
    CompassOverlay mCompassOverlay;
    MyLocationNewOverlay mLocationOverlay;
    LocationManager lm;
    android.location.Location currentLocation = null;

    Date lastUpdate = new Date();

    boolean showedInfo = false;

    int zoom = Helper.mapZoom;
    GeoPoint center = Helper.mapCenter;
    float rotation = Helper.mapRotation;

    public MapFragment() {
    }

    private void saveMapState() {
        zoom = map.getZoomLevel();
        center =  new GeoPoint(map.getMapCenter().getLatitude(), map.getMapCenter().getLongitude());
        rotation = map.getMapOrientation();
    }

    private void restoreMapState() {
        mapController.setZoom(zoom);
        mapController.setCenter(center);
        map.setMapOrientation(rotation);
    }

    private void setMarker() {
        ArrayList<OverlayItem> items = new ArrayList<>();
        final boolean festivalRunning = Helper.FestivalStart.before(new Date()) &&
                Helper.FestivalEnd.after(new Date());//*/

        // collect OverlayItems
        if(festivalRunning) { // Festival läuft: Events sammeln
            // aktuelle Events sammeln
            Date now = new GregorianCalendar(2016, 6-1, 19, 20, 43).getTime();
            for (Event event : ManageData.getEvents(now, now)) {
                Location location = ManageData.getLocation(event.locationId);
                OverlayItem item = new OverlayItem(event.id, null, null,
                        new GeoPoint(location.lat, location.lng));
                item.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
                items.add(item);
            }
        } else { // Festival läuft nicht: alle Locations zusammensammeln
            for (Location location : ManageData.getLocations()) {
                OverlayItem item = new OverlayItem(location.id, null, null,
                        new GeoPoint(location.lat, location.lng));
                item.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
                items.add(item);
            }
        }

        //the overlay
        mMarkerOverlay = new ItemizedIconOverlay<OverlayItem>(
            items,
            ResourcesCompat.getDrawable(getResources(), R.drawable.marker_default, null),
            new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                @Override
                public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                    // focus changed
                    if(lastFocus != null) {
                        lastFocus.setMarker(ResourcesCompat.getDrawable(
                                getResources(), R.drawable.marker_default, null
                        ));
                    }
                    lastFocus = item;
                    item.setMarker(ResourcesCompat.getDrawable(
                            getResources(), R.drawable.marker_default_focused_base, null
                    ));

                    map.invalidate();

                    if(festivalRunning) { // open Event-Dialog
                        Event e = ManageData.getEvent(item.getUid());
                        Location l = ManageData.getLocation(e.locationId);

                        View info = View.inflate(getContext(), R.layout.listitem_event, null);
                        TextView textDate = (TextView) info.findViewById(R.id.textDate);
                        TextView textTitle = (TextView) info.findViewById(R.id.textTitle);
                        TextView textLocation = (TextView) info.findViewById(R.id.textLocation);
                        TextView textUrl = (TextView) info.findViewById(R.id.textUrls);
                        TextView textText = (TextView) info.findViewById(R.id.textText);

                        textTitle.setText(e.title);

                        if(e.text.length() == 0) {
                            textText.setVisibility(View.GONE);
                        } else {
                            textText.setText(e.text);
                            textText.setVisibility(View.VISIBLE);
                        }

                        if(e.url.length() == 0 || e.url.contains("brn-schwafelrunde.de")) {
                            textUrl.setVisibility(View.GONE);
                        } else {
                            textUrl.setText(e.url);
                            textUrl.setVisibility(View.VISIBLE);
                        }

                        // date
                        SimpleDateFormat time = new SimpleDateFormat("HH:mm 'Uhr'", Locale.GERMANY);
                        SimpleDateFormat weekDay = new SimpleDateFormat("EEEE", Locale.GERMANY);
                        Date start = e.dateStart;
                        Date end = e.dateEnd;

                        String date = weekDay.format(start) + ", " + time.format(start) + " - ";
                        if(! weekDay.format(start).equals(weekDay.format(end))) {
                            date += weekDay.format(end) + ", ";
                        }
                        date += time.format(end);

                        textDate.setText(date);

                        // location
                        if (l != null) {
                            textLocation.setText(l.name + " || " + l.address);
                            textLocation.setVisibility(View.VISIBLE);
                        } else {
                            textLocation.setVisibility(View.GONE);
                        }

                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setView(info)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // do nothing
                                    }
                                });
                        builder.show();
                    } else { // open locations
                        int position =
                                ((LocationArrayAdapter) Helper.locationsFragment.lvLocations.getAdapter())
                                        .getPosition(item.getUid());
                        if (position > -1) {
                            Helper.locationsFragment.lvLocations.setSelection(position);
                            Helper.mainActivity.mViewPager.setCurrentItem(1);
                        }
                    }

                    return true;
                }
                @Override
                public boolean onItemLongPress(final int index, final OverlayItem item) {
                    return false;
                }
            },
            mResourceProxy
        );

        if(! map.getOverlays().contains(mMarkerOverlay)) map.getOverlays().add(mMarkerOverlay);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("mapZoom", map.getZoomLevel());
        outState.putInt("mapCenterLat", map.getMapCenter().getLatitudeE6());
        outState.putInt("mapCenterLng", map.getMapCenter().getLongitudeE6());
        outState.putFloat("mapRotation", map.getMapOrientation());
        saveMapState();
    }

    @Override
    public void onPause() {
        saveMapState();
        super.onPause();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(this.isVisible()) {
            // Info zeigen, dass erst ab Freitag die aktuellen Sachen angezeigt werden
            if(! showedInfo) {
                showedInfo = true;

                if(Helper.FestivalStart.after(new Date())) { // wir haben noch nicht den 17.6.
                    Helper.message(this.getContext(), getString(R.string.not_started_yet));
                }
            }

            // Ansicht ggf. aktualisieren
            Date beforeFiveMinutes = new Date(new Date().getTime() - 1000 * 60 * 5);
            if (lastUpdate.before(beforeFiveMinutes)) {
                lastUpdate = new Date();
                setMarker();
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Helper.mapFragment = this;

        // Die Map
        mResourceProxy = new ResourceProxyImpl(inflater.getContext().getApplicationContext());
        map = new MapView(inflater.getContext(), mResourceProxy);
        map.setLayerType(View.LAYER_TYPE_SOFTWARE, null); // disable hardware acceleration

        File tiles = new File(Helper.mainActivity.destinationPath);
        if(tiles.exists()) {
            map.setTileSource(
                    new XYTileSource("brn2016", 0, 22, 256, ".png",
                            new String[]{
                                    "http://{a,b,c}.tiles.wmflabs.org/hikebike/{z}/{x}/{y}.png"
                            }
                    )
            );
        } else {
            map.setTileSource(TileSourceFactory.HIKEBIKEMAP);
        }

        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.setMaxZoomLevel(Helper.mapMaxZoom);
        map.setFlingEnabled(true);

        mapController = map.getController();

        // Overlays
        map.getOverlays().clear();

        // Kompass
        /*mCompassOverlay = new CompassOverlay(getContext(),
                new InternalCompassOrientationProvider(getContext()), map);
        map.getOverlays().add(mCompassOverlay);
        mCompassOverlay.enableCompass();*/

        // Rotation
        mRotationGestureOverlay = new RotationGestureOverlay(getContext(), map);
        mRotationGestureOverlay.setEnabled(true);
        map.getOverlays().add(mRotationGestureOverlay);

        // Locations
        setMarker();

        // savedInstance
        if(savedInstanceState != null) {
            int savedZoom = savedInstanceState.getInt("mapZoom", 0);
            if(savedZoom != 0) zoom = savedZoom;

            int lat = savedInstanceState.getInt("mapCenterLat", 0);
            int lng = savedInstanceState.getInt("mapCenterLng", 0);
            if(lat != 0 && lng != 0) center = new GeoPoint(lat, lng);

            float savedRotation = savedInstanceState.getFloat("mapRotation", 0);
            if(savedRotation != 0) rotation = savedRotation;
        }

        restoreMapState();

        setHasOptionsMenu(true);

        return map;
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_map, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_location:
                if(map.getOverlays().contains(mLocationOverlay)) {
                    // already activated
                    /*mLocationOverlay.disableMyLocation();
                    mLocationOverlay.disableFollowLocation();
                    map.getOverlays().remove(mLocationOverlay);

                    map.invalidate();*/
                } else { // GPS tracking
                    // Location manager
                    lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0l, 0f, this);

                        mLocationOverlay = new MyLocationNewOverlay(getContext(),
                                new GpsMyLocationProvider(getContext()), map);
                        mLocationOverlay.setDrawAccuracyEnabled(true);

                        mLocationOverlay.setPersonIcon(BitmapFactory.decodeResource(
                                getContext().getResources(), R.drawable.person));
                        mLocationOverlay.setPersonHotspot(15.0f, 15.0f);

                        mLocationOverlay.runOnFirstFix(new Runnable() {
                            @Override
                            public void run() {
                                //mLocationOverlay.disableFollowLocation();
                                mapController.animateTo(mLocationOverlay.getMyLocation());
                            }
                        });
                        map.getOverlays().add(mLocationOverlay);

                        mLocationOverlay.enableMyLocation();
                        //mLocationOverlay.enableFollowLocation();

                        map.invalidate();
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                                Manifest.permission.ACCESS_FINE_LOCATION)) {
                            // falls schon mehrfach abgelehnt wurde zeige Nachricht
                            Helper.message(getContext(), getString(R.string.no_access_to_location));
                        } else {
                            // ask for permission
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    Helper.PERMISSION_ACCESS_LOCATION);
                        }
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onLocationChanged(android.location.Location location) {
        currentLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
