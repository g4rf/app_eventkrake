package de.sophvaerck.eventkrake;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.ProgressBar;

import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;

import java.io.File;
import java.util.Date;

import de.sophvaerck.eventkrake.Helper.Helper;
import de.sophvaerck.eventkrake.Helper.ManageData;
import de.sophvaerck.eventkrake.Helper.MapViewPager;

public class MainActivity extends AppCompatActivity {
    public Menu mainMenu;

    String assetName = "naf2016.zip";
    String destinationPath = OpenStreetMapTileProviderConstants.getBasePath().getPath()
            + "/" + assetName;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    public ViewPager mViewPager;

    private void copyMapTiles() {
        if (Helper.copyAssetFile(Helper.context.getAssets(), assetName, destinationPath)) {
            // reload MapFragment
            if(Helper.mapFragment != null && Helper.mapFragment.map != null) {
                Helper.mapFragment.map.invalidate();
            }
        } else {

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Helper.mainActivity = this;
        Helper.context = this.getBaseContext();
        Helper.working = (ProgressBar) findViewById(R.id.working);
        setContentView(R.layout.activity_main);

        // Events cachen
        ManageData.getEvents();
        ManageData.getLocations();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (MapViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(3);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // copy tiles to osmdroid path if it doesn't exist or it's too old (> 1m)
        File destinationFile = new File(destinationPath);
        Date oneMonthAgo = new Date(new Date().getTime() - 1000 * 60 * 60 * 24 * 31);
        if (! destinationFile.exists() ||
                new Date(destinationFile.lastModified()).before(oneMonthAgo)) {
            // ask for permission to write to external storage
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                copyMapTiles();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // falls schon mehrfach abgelehnt wurde zeige Nachricht
                    Helper.message(this, getString(R.string.no_access_to_external_storage));
                } else {
                    // ask for permission
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            Helper.PERMISSION_WRITE_MAP);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mainMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_info:
                Helper.message(this, getString(R.string.info_text));
                return true;
            case R.id.action_search:
                return false;
            case R.id.action_location:
                return false;
            case R.id.action_time:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setMessage("Das gesamte Programm anzeigen oder vergangene Veranstaltungen" +
                        " ausblenden?")
                        .setPositiveButton("Gesamtes Programm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Helper.changeTime(true);
                            }
                        })
                        .setNegativeButton("Vergangenes ausblenden", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Helper.changeTime(false);
                            }
                        });

                builder.show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[],
                                           int[] grantResults) {
        // nothing granted
        if(grantResults.length == 0) return;

        switch (requestCode) {
            case Helper.PERMISSION_WRITE_MAP:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    copyMapTiles();
                }
                break;
            case Helper.PERMISSION_ACCESS_LOCATION:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        Helper.mapFragment.lm.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER, 0l, 0f, Helper.mapFragment);

                        Helper.mapFragment.mLocationOverlay.enableMyLocation();
                        Helper.mapFragment.mLocationOverlay.enableFollowLocation();

                        Helper.mapFragment.trackLocation = true;
                    }
                }
                break;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            switch (position) {
                case 0:
                    return new EventsFragment();
                case 1:
                    return new LocationsFragment();
                case 2:
                    return new MapFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Zeitplan";
                case 1:
                    return "Orte";
                case 2:
                    return "Was läuft gerade?";
            }
            return null;
        }
    }
}
