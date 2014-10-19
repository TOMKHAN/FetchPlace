package hr.foi.tosulc.fetchplace.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by tosulc on 15.10.2014..
 */
public class AppLocationService extends Service implements LocationListener {

    protected LocationManager locationManager;
    private LatLng latLngLocation = null;

    public AppLocationService(Context context) {
        locationManager = (LocationManager) context
                .getSystemService(LOCATION_SERVICE);
    }

    public LatLng getLocation(Context ctx) {
        LocationManager locationManager = (LocationManager) ctx
                .getSystemService(ctx.LOCATION_SERVICE);
        boolean isGPSEnabled, isNetworkEnabled;
        Location location = null;
        try {
            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            10,
                            120000, this);
                    if (locationManager != null) {
                        location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latLngLocation = new LatLng(location.getLatitude(),
                                    location.getLongitude());
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 120000, this);
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latLngLocation = new LatLng(location.getLatitude(),
                                        location.getLongitude());
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return latLngLocation;
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

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
