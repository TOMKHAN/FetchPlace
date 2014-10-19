package hr.foi.tosulc.fetchplace.json;


import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import hr.foi.tosulc.fetchplace.types.Place;

/**
 * Created by tosulc on 17.10.2014..
 */
public class JSONPlaceGetterFromKeyword {

    public Place getPlaceFromKeyword(String keyword) {
        JSONObject json = null;

        try {
            json = new JSONObject(JSONParser.getJSONFromUrl("https://maps.googleapis.com/maps/api/place/textsearch/json?query=" + keyword + "&key=AIzaSyCbDkfctAvtaI1kcySbd-jqtkYjDUQ0yy0"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONArray jsonFirstPlace = json.getJSONArray("results");
            JSONObject jsonPlace = jsonFirstPlace.getJSONObject(0);
            JSONObject jsonPlaceLocation = jsonPlace.getJSONObject("geometry").getJSONObject("location");
            LatLng addressLocation = new LatLng(Double.parseDouble(jsonPlaceLocation.getString("lat")), Double.parseDouble(jsonPlaceLocation.getString("lng")));
            Place placeFound = new Place(1, jsonPlace.getString("name"), jsonPlace.getString("formatted_address"), addressLocation);
            return placeFound;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

}

