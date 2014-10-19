package hr.foi.tosulc.fetchplace;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.viewpagerindicator.PageIndicator;
import com.viewpagerindicator.TabPageIndicator;

import hr.foi.tosulc.fetchplace.adapters.TabsAdapter;
import hr.foi.tosulc.fetchplace.types.Place;

/**
 * Created by tosulc on 15.10.2014..
 */
public class FetchPlace extends FragmentActivity {

    private ViewPager viewPager;
    private TabsAdapter mAdapter;
    private PageIndicator mIndicator;
    private static float ZOOM_LEVEL = 18;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fetch_place);

        mAdapter = new TabsAdapter(getSupportFragmentManager());

        viewPager = (ViewPager) findViewById(R.id.vp_fetch_places);
        viewPager.setAdapter(mAdapter);

        mIndicator = (TabPageIndicator) findViewById(R.id.tpi_fetch_places);
        mIndicator.setViewPager(viewPager);

        mIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //transition to maps
                if (position == 0) {
                    Place place = MapFragment.getNearestLatLngPlaceFromDB(getApplicationContext());
                    if (place != null) {
                        MapFragment.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLocation(), ZOOM_LEVEL));
                        MapFragment.addLocationMarker(place.getLocation(), place.getName(), place.getAddress());
                    }
                }
                //transition to database
                else if (position == 1) {
                    Place place = MapFragment.getNearestLatLngPlaceFromDB(getApplicationContext());
                    if (place != null) {
                        DatabaseFragment.updateLocationInfoOnDatabaseFragment(place);
                    }

                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.fetch_place, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
