package de.sophvaerck.eventkrake;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.sophvaerck.eventkrake.Helper.Event;
import de.sophvaerck.eventkrake.Helper.Helper;
import de.sophvaerck.eventkrake.Helper.Location;
import de.sophvaerck.eventkrake.Helper.LocationArrayAdapter;
import de.sophvaerck.eventkrake.Helper.ManageData;

public class MapFragment extends Fragment implements LocationListener {
    ResourceProxyImpl mResourceProxy;
    MapView map;
    IMapController mapController;
    RotationGestureOverlay mRotationGestureOverlay;
    ItemizedIconOverlay<OverlayItem> mMarkerOverlay;
    ItemizedIconOverlay<OverlayItem> mToiletOverlay;
    ItemizedIconOverlay<OverlayItem> mToiletDisabledOverlay;
    OverlayItem lastFocus;
    CompassOverlay mCompassOverlay;
    MyLocationNewOverlay mLocationOverlay;
    LocationManager lm;
    android.location.Location currentLocation = null;

    Date lastUpdate = new Date();

    boolean showedInfo = false;

    boolean trackLocation = false;
    boolean haveFix = false;

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
                Helper.FestivalEnd.after(new Date());

        // events / locations
        if(festivalRunning) { // Festival läuft: Events sammeln
            // aktuelle Events sammeln
            Date now = new Date();
            for (Event event : ManageData.getEvents(now, now)) {
                Location location = ManageData.getLocation(event.locationId);
                OverlayItem item = new OverlayItem(event.id, "event", null,
                        new GeoPoint(location.lat, location.lng));
                item.setMarker(ResourcesCompat.getDrawable(getResources(), R.drawable.marker_default, null));
                item.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
                items.add(item);
            }
        } else { // Festival läuft nicht: alle Locations zusammensammeln
            for (Location location : ManageData.getLocations()) {
                OverlayItem item = new OverlayItem(location.id, "location", null,
                        new GeoPoint(location.lat, location.lng));
                item.setMarker(ResourcesCompat.getDrawable(getResources(), R.drawable.marker_default, null));
                item.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
                items.add(item);
            }
        }

        // FIXME Hardcoded is no good. Check location items or similar to create it from data.
        /*** toilets ***/
        // Martin-Luther-Platz, Pulsnitzer Straße 6
        OverlayItem wcMLP = new OverlayItem("wcMLP", "toilet", null,
                new GeoPoint(51.06401, 13.75780));
        wcMLP.setMarker(ResourcesCompat.getDrawable(getResources(), R.drawable.wc_brn, null));
        wcMLP.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
        items.add(wcMLP);
        // Alaunstraße Ecke Louisenstraße
        OverlayItem wcAEL = new OverlayItem("wcAEL", "toilet", null,
                new GeoPoint(51.06670, 13.75222));
        wcAEL.setMarker(ResourcesCompat.getDrawable(getResources(), R.drawable.wc_brn, null));
        wcAEL.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
        items.add(wcAEL);
        // Rothenburger Straße 21
        OverlayItem wcRo21 = new OverlayItem("wcRo21", "toilet", null,
                new GeoPoint(51.06461, 13.75277));
        wcRo21.setMarker(ResourcesCompat.getDrawable(getResources(), R.drawable.wc_brn, null));
        wcRo21.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
        items.add(wcRo21);
        // Tanzstube (Parkplatz Alaunstraße Ecke Sebnitzer Straße)
        OverlayItem wcTanzstube = new OverlayItem("wcTanzstube", "toilet", null,
                new GeoPoint(51.06919, 13.75421));
        wcTanzstube.setMarker(ResourcesCompat.getDrawable(getResources(), R.drawable.wc_brn, null));
        wcTanzstube.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
        items.add(wcTanzstube);
        // Sebnitzer Straße 31
        OverlayItem wcSe31 = new OverlayItem("wcSe31", "toilet", null,
                new GeoPoint(51.06842, 13.75720));
        wcSe31.setMarker(ResourcesCompat.getDrawable(getResources(), R.drawable.wc_brn, null));
        wcSe31.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
        items.add(wcSe31);
        // Scheune
        OverlayItem wcScheune = new OverlayItem("wcScheune", "toilet", null,
                new GeoPoint(51.06629, 13.75141));
        wcScheune.setMarker(ResourcesCompat.getDrawable(getResources(), R.drawable.wc_brn_dis, null));
        wcScheune.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
        items.add(wcScheune);
        // Panama
        OverlayItem wcPanama = new OverlayItem("wcPanama", "toilet", null,
                new GeoPoint(51.06724, 13.75557));
        wcPanama.setMarker(ResourcesCompat.getDrawable(getResources(), R.drawable.wc_brn_dis, null));
        wcPanama.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
        items.add(wcPanama);
        // Elbsalon
        OverlayItem wcElbsalon = new OverlayItem("wcElbsalon", "toilet", null,
                new GeoPoint(51.07126, 13.75082));
        wcElbsalon.setMarker(ResourcesCompat.getDrawable(getResources(), R.drawable.wc_brn_dis, null));
        wcElbsalon.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
        items.add(wcElbsalon);

        // the marker overlay
        mMarkerOverlay = new ItemizedIconOverlay<OverlayItem>(
                items,
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        if(item.getTitle().equals("toilet")) return false;

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
                                textText.setText(Html.fromHtml(e.text));
                                textText.setVisibility(View.VISIBLE);
                            }

                            if(e.url.length() == 0 || e.url.contains("brn-buero.de")) {
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
        super.onPause();
        saveMapState();

        if(trackLocation) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                lm.removeUpdates(this);
                mLocationOverlay.disableMyLocation();
                mLocationOverlay.disableFollowLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if(trackLocation) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0l, 0f, this);
                mLocationOverlay.enableMyLocation();
                mLocationOverlay.enableFollowLocation();
            }
        }
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

        // Location manager
        lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

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
        map.setMinZoomLevel(Helper.mapMinZoom);
        map.setFlingEnabled(true);

        mapController = map.getController();

        // Overlays
        map.getOverlays().clear();

        // Location overlay
        mLocationOverlay = new MyLocationNewOverlay(getContext(),
                new GpsMyLocationProvider(getContext()), map);
        mLocationOverlay.setDrawAccuracyEnabled(true);

        mLocationOverlay.setPersonIcon(BitmapFactory.decodeResource(
                getContext().getResources(), R.drawable.person));
        mLocationOverlay.setPersonHotspot(15.0f, 15.0f);

        /*mLocationOverlay.runOnFirstFix(new Runnable() {
            @Override
            public void run() {
                haveFix = true;
                try {
                    mapController.animateTo(mLocationOverlay.getMyLocation());
                    mapController.setZoom(17);
                } catch(Exception e) {
                    Log.d("ERROR", "runOnFirstfix", e);
                }
            }
        });*/
        map.getOverlays().add(mLocationOverlay);

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
                if(trackLocation) { // is activated
                    lm.removeUpdates(this);
                    mLocationOverlay.disableMyLocation();
                    mLocationOverlay.disableFollowLocation();
                    map.invalidate();
                    trackLocation = false;
                    item.setIcon(android.R.drawable.ic_menu_mylocation)
                        .setTitle(R.string.action_location);
                } else { // start GPS tracking
                    if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                        Toast.makeText(getContext(), "Bitte warte, Standort wird ermittelt...",
                                Toast.LENGTH_LONG).show();
                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                        alertDialogBuilder
                                .setMessage("GPS ist deaktiviert. Die Einstellungen aufrufen um es" +
                                        " zu aktivieren?")
                                .setCancelable(false)
                                .setPositiveButton("Einstellungen",
                                        new DialogInterface.OnClickListener(){
                                            public void onClick(DialogInterface dialog, int id){
                                                startActivity(new Intent(
                                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
                                                ));
                                            }
                                        })
                                .setNegativeButton("Abbrechen",
                                        new DialogInterface.OnClickListener(){
                                            public void onClick(DialogInterface dialog, int id){
                                                dialog.cancel();
                                            }
                                }).show();

                        return true;
                    }

                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0l, 0f, this);

                        mLocationOverlay.enableMyLocation();
                        mLocationOverlay.enableFollowLocation();

                        map.invalidate();

                        trackLocation = true;
                        item.setIcon(ResourcesCompat.getDrawable(
                                getResources(), R.drawable.ic_menu_disable_mylocation, null))
                            .setTitle(R.string.action_disable_location);
                        /*if(haveFix) {
                            try {
                                mapController.animateTo(mLocationOverlay.getMyLocation());
                                mapController.setZoom(17);
                            } catch(Exception e) {
                                Log.d("ERROR", "haveFix", e);
                            }
                        }*/
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
