package hr.foi.tosulc.fetchplace.json;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import hr.foi.tosulc.fetchplace.MapFragment;
import hr.foi.tosulc.fetchplace.types.Place;

/**
 * Created by tosulc on 15.10.2014..
 */
public class JSONPlaceGetter implements PlaceGetter{
    public static final String TAG = "FetchPlace";
    @Override
    public Place getLocationPlace(LatLng location, Context ctx) {
        JSONObject json = null;

        try {
          json = new JSONObject(JSONParser.getJSONFromUrl("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+location.latitude+","+location.longitude+"&radius=" + getRadiusFromSharedPreferences(ctx) +"&key=AIzaSyCbDkfctAvtaI1kcySbd-jqtkYjDUQ0yy0"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONArray jsonFirstPlace = json.getJSONArray("results");
            int random_number_place = new Random().nextInt(jsonFirstPlace.length());
            JSONObject jsonPlace = jsonFirstPlace.getJSONObject(random_number_place);
            JSONObject jsonPlaceLocation = jsonPlace.getJSONObject("geometry").getJSONObject("location");
            LatLng addressLocation = new LatLng(Double.parseDouble(jsonPlaceLocation.getString("lat")),Double.parseDouble(jsonPlaceLocation.getString("lng")));
            Place firstPlaceFound = new Place(1,jsonPlace.getString("name"),jsonPlace.getString("vicinity"),addressLocation);
            return firstPlaceFound;

            /* get first in list
            for (int i=0; i<jsonFirstPlace.length(); i++){
                JSONObject jsonPlace = jsonFirstPlace.getJSONObject(i);
                JSONObject jsonPlaceLocation = jsonPlace.getJSONObject("geometry").getJSONObject("location");
                //Log.w("FetchPlace", "Lokacija s kordinatama: " + jsonPlaceLocation.getString("lat") + "," + jsonPlaceLocation.getString("lng"));
                LatLng addressLocation = new LatLng(Double.parseDouble(jsonPlaceLocation.getString("lat")),Double.parseDouble(jsonPlaceLocation.getString("lng")));
                Place firstPlaceFound = new Place(i,jsonPlace.getString("name"),jsonPlace.getString("vicinity"),addressLocation);
                return firstPlaceFound;
            }*/


        }catch (JSONException e){
            e.printStackTrace();
        }
        return null;
    }

    public float getRadiusFromSharedPreferences(Context ctx) {
        SharedPreferences sharedPreferences = ctx
                .getSharedPreferences(MapFragment.ARG_PREFS_NAME, 0);
        float radiusValue = sharedPreferences
                .getFloat("radius", 200);
        return radiusValue;
    }
}
