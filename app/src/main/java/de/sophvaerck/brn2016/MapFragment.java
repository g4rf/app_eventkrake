package de.sophvaerck.brn2016;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.res.ResourcesCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.ResourceProxyImpl;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.compass.CompassOverlay;
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.sophvaerck.brn2016.Helper.Helper;
import de.sophvaerck.brn2016.Helper.Location;
import de.sophvaerck.brn2016.Helper.ManageData;

public class MapFragment extends Fragment {
    ResourceProxyImpl mResourceProxy;
    MapView map;
    IMapController mapController;
    RotationGestureOverlay mRotationGestureOverlay;
    ItemizedIconOverlay<OverlayItem> mMarkerOverlay;
    OverlayItem lastFocus;
    CompassOverlay mCompassOverlay;

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
        for(Map.Entry<String, Location> location: ManageData.getLocations().entrySet()) {
            items.add(new OverlayItem(location.getValue().id, null, null,
                    new GeoPoint(location.getValue().lat, location.getValue().lng))
            );
        }

        //the overlay
        mMarkerOverlay = new ItemizedIconOverlay<OverlayItem>(
                items,
                ResourcesCompat.getDrawable(getResources(), R.drawable.marker_default, null),
                new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                    @Override
                    public boolean onItemSingleTapUp(final int index, final OverlayItem item) {
                        // open locations


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Die Map
        mResourceProxy = new ResourceProxyImpl(inflater.getContext().getApplicationContext());
        map = new MapView(inflater.getContext(), mResourceProxy);

        // copy tiles to osmdroid path
        if (! copyAssetFile(Helper.context.getAssets(), "brn2016.zip",
                OpenStreetMapTileProviderConstants.getBasePath().getPath())) {
            Helper.error("Kein Zugriff auf externen Speicher möglich. Karte kann nicht geladen " +
                    "werden.");
        }
        //map.setTileSource(TileSourceFactory.HIKEBIKEMAP);
        //map.setUseDataConnection(false); // nur Offline
        map.setTileSource(
                new XYTileSource("brn2016", 0, 22, 256, ".png",
                        new String[] {
                                "http://{a,b,c}.tiles.wmflabs.org/hikebike/{z}/{x}/{y}.png"
                        }
                )
        );

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

    private boolean copyAssetFile(final AssetManager assetManager,
                                        final String assetName,
                                        final String destinationDirectory) {
        InputStream in;
        OutputStream out;
        final String destPath = destinationDirectory + "/" + assetName;
        final File destFile = new File(destPath);

        // copy file only if it doesn't exist yet or if it's too old (24h)
        Date yesterday = new Date(new Date().getTime() - 1000 * 60 * 60 * 24);
        if (! destFile.exists() || new Date(destFile.lastModified()).before(yesterday)) {
            //Log.d(LOG_TAG, String.format(
            //       "Copy %s map archive in assets into %s", assetRelativePath, newfilePath));
            try {
                final File directory = destFile.getParentFile();
                if (! directory.exists()) {
                    if (directory.mkdirs()) {
                        // Log.d(LOG_TAG, "Directory created: " + directory.getAbsolutePath());
                    }
                }
                in = assetManager.open(assetName);
                out = new FileOutputStream(destPath);
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
        }
        return true;
    }
}
