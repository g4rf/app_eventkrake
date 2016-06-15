package de.sophvaerck.brn2016;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;

import de.sophvaerck.brn2016.Helper.Event;
import de.sophvaerck.brn2016.Helper.Helper;
import de.sophvaerck.brn2016.Helper.Location;
import de.sophvaerck.brn2016.Helper.LocationArrayAdapter;
import de.sophvaerck.brn2016.Helper.ManageData;

public class MapFragment extends Fragment {
    ResourceProxyImpl mResourceProxy;
    MapView map;
    IMapController mapController;
    RotationGestureOverlay mRotationGestureOverlay;
    ItemizedIconOverlay<OverlayItem> mMarkerOverlay;
    OverlayItem lastFocus;
    CompassOverlay mCompassOverlay;

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

        // collect OverlayItems
        if(Helper.FestivalStart.after(new Date())) { // wir haben noch nicht den 17.6.
            // alle Locations zusammensammeln
            for (Location location : ManageData.getLocations()) {
                OverlayItem item = new OverlayItem(location.id, null, null,
                        new GeoPoint(location.lat, location.lng));
                item.setMarkerHotspot(OverlayItem.HotspotPlace.CENTER);
                items.add(item);
            }
        } else {
            // aktuelle Events sammeln
            Date now = new Date(); //new GregorianCalendar(2016, 6-1, 18, 18, 43).getTime();
            for (Event event : ManageData.getEvents(now, now)) {
                Location location = ManageData.getLocation(event.locationId);
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

                    // open locations
                    int position =
                        ((LocationArrayAdapter)Helper.locationsFragment.lvLocations.getAdapter())
                            .getPosition(item.getUid());
                    if(position > -1) {
                        Helper.locationsFragment.lvLocations.setSelection(position);
                        Helper.mainActivity.mViewPager.setCurrentItem(1);
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

            // Anzeige ändern
            Helper.changeTime(false);

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

        mapController = map.getController();

        // Overlays
        map.getOverlays().clear();

        // Kompass
        /*mCompassOverlay = new CompassOverlay(map.getContext(),
                new InternalCompassOrientationProvider(map.getContext()), map);
        map.getOverlays().add(this.mCompassOverlay); */

        // Rotation
        mRotationGestureOverlay = new RotationGestureOverlay(map.getContext(), map);
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

        return map;
    }
}
