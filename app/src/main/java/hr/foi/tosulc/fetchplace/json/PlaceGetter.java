package hr.foi.tosulc.fetchplace.json;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import java.lang.reflect.Array;
import java.util.ArrayList;

import hr.foi.tosulc.fetchplace.types.Place;

/**
 * Created by tosulc on 16.10.2014..
 */
public interface PlaceGetter {
    public ArrayList<Place> getLocationPlace(LatLng location, Context ctx);
}
