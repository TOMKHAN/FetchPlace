package hr.foi.tosulc.fetchplace.adapters;

import hr.foi.tosulc.fetchplace.DatabaseFragment;
import hr.foi.tosulc.fetchplace.MapFragment;
import hr.foi.tosulc.fetchplace.PhotoFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by tosulc on 15.10.2014..
 */
public class TabsAdapter extends FragmentPagerAdapter {

    private int mCount = 3;
    private static String[] TAB_TITLES = {"Map", "Database", "Photo"};

    public TabsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                Fragment fragment = new MapFragment();
                return fragment;

            case 1:
                Fragment database = new DatabaseFragment();
                return database;
            case 2:
                Fragment photo = new PhotoFragment();
                return photo;
        }
        return null;
    }


    @Override
    public int getCount() {
        return mCount;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TabsAdapter.TAB_TITLES[position];
    }


}
