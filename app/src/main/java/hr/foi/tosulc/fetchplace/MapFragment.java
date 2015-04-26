package hr.foi.tosulc.fetchplace;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import hr.foi.tosulc.fetchplace.adapters.PlaceAdapter;
import hr.foi.tosulc.fetchplace.helpers.ConnectionDetector;
import hr.foi.tosulc.fetchplace.json.JSONPlaceGetter;
import hr.foi.tosulc.fetchplace.services.AppLocationService;
import hr.foi.tosulc.fetchplace.types.Place;

/**
 * Created by tosulc on 14.10.2014..
 */
public class MapFragment extends Fragment implements GoogleMap.OnMarkerClickListener, GoogleMap.OnInfoWindowClickListener {
    //TODO API key promjenititiiiiiiiiiiiiiiiiii!
    public static final String TAG = "FetchPlace";
    public static final String ARG_PREFS_NAME = "apps_shared_prefs";
    private static float ZOOM_LEVEL = 18;
    private static LatLng STARTING_COORDINATES = new LatLng(46.307506, 16.33829); //for init only

    private SupportMapFragment mMapFragment;
    public static GoogleMap mMap = null;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        ImageButton btnGetNearestPlace = (ImageButton) rootView.findViewById(R.id.btn_fetch_place);
        btnGetNearestPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppLocationService als = new AppLocationService(getActivity().getApplicationContext());
                LatLng userLocation = als.getLocation(getActivity());
                if (userLocation == null) {
                    Toast.makeText(getActivity().getApplicationContext(), getString(R.string.location_unknown),Toast.LENGTH_SHORT).show();
                } else {
                    ConnectionDetector cd = new ConnectionDetector(getActivity().getApplicationContext());
                    if (cd.isConnectedToInternet()) {
                        new GetNearestLocationCoordinates().execute(userLocation);
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), getString(R.string.no_internet_for_fetching), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        FragmentManager fm = getChildFragmentManager();
        mMapFragment = (SupportMapFragment) fm.findFragmentById(R.id.f_map);
        if (mMapFragment == null) {
            mMapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.f_map, mMapFragment).commit();
            ConnectionDetector cd = new ConnectionDetector(getActivity().getApplicationContext());
            if (!cd.isConnectedToInternet()){
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.no_internet_for_map), Toast.LENGTH_LONG).show();
            }
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMap = null;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mMap == null) {
            mMap = mMapFragment.getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        mMap.setOnMarkerClickListener(this);

        mMap.setOnInfoWindowClickListener(this);

        //if there is no place in db -> exception and search for GPS location, if there is no from GPS location-> then show test marker!
        //in catch section when there is no location in db -> that means
        LatLng latlng;
        try {
            Place place = getNearestLatLngPlaceFromDB(getActivity().getApplicationContext());
            addLocationMarker(place.getLocation(),place.getName(),place.getAddress());
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(place.getLocation(), ZOOM_LEVEL)));
        } catch (Exception e) {
            AppLocationService als = new AppLocationService(getActivity().getApplicationContext());
            latlng = als.getLocation(getActivity());
            if (latlng == null) {
                latlng = new LatLng(STARTING_COORDINATES.latitude, STARTING_COORDINATES.longitude);
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(latlng, ZOOM_LEVEL)));
                addLocationMarker(latlng, getString(R.string.test_marker_title), getString(R.string.test_marker_message));
            } else {
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.fromLatLngZoom(latlng, ZOOM_LEVEL)));
                addLocationMarker(latlng, getString(R.string.test_marker_title), getString(R.string.test_marker_message));
            }
        }

    }

    public static void addLocationMarker(LatLng latlng, String name, String address) {
        mMap.addMarker(createCustomMarker(latlng, name, address));

    }

    public static MarkerOptions createCustomMarker(LatLng latLng, String name, String address) {
        return new MarkerOptions().position(latLng).title(name).snippet(address);
    }


    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    public static Place getNearestLatLngPlaceFromDB(Context context) {
        PlaceAdapter pa = new PlaceAdapter(context);
        pa.openToRead();
        Place place = pa.getPlaceFromDB();
        pa.close();
        return place;
    }

    private class GetNearestLocationCoordinates extends AsyncTask<LatLng, Void, ArrayList<Place>> {
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setTitle(getString(R.string.app_name));
            progressDialog.setMessage(getString(R.string.fetching_place));
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<Place> doInBackground(LatLng... location) {
            ArrayList<Place> places = new JSONPlaceGetter().getLocationPlace(location[0], getActivity().getApplicationContext());
            return places;
        }

        @Override
        protected void onPostExecute(ArrayList<Place> places) {
            //save in DB
            PlaceAdapter pa = new PlaceAdapter(getActivity().getApplicationContext());
            pa.openToWrite();
            for (Place p : places) {
                pa.updatePlace(p);
            }
            pa.close();

            //animation to location
            if (places != null) {
                for (Place p : places) {
                    addLocationMarker(p.getLocation(), p.getName(), p.getAddress());
                    Log.w("Repsly", "Dodano: " + p.getName());
                }
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(places.get(0).getLocation(), ZOOM_LEVEL));
            }
            // Dismiss the progress dialog
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }
}
