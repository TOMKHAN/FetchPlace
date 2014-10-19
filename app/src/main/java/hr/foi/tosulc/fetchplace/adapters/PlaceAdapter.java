package hr.foi.tosulc.fetchplace.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.android.gms.maps.model.LatLng;

import hr.foi.tosulc.fetchplace.data.DBHelper;
import hr.foi.tosulc.fetchplace.types.Place;

/**
 * Created by tosulc on 15.9.2014..
 */
public class PlaceAdapter {
    public static final String TABLE = "place";

    private DBHelper mHelper;
    private SQLiteDatabase mDatabase;
    private Context mContext;

    public PlaceAdapter(Context c) {
        mContext = c;
    }

    public PlaceAdapter openToRead() throws android.database.SQLException {
        mHelper = new DBHelper(mContext);
        mDatabase = mHelper.getReadableDatabase();
        return this;
    }

    public PlaceAdapter openToWrite() throws android.database.SQLException {
        mHelper = new DBHelper(mContext);
        mDatabase = mHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        mDatabase.close();
    }

    public Place getPlaceFromDB() {
        Place place;

        String[] columns = new String[]{"id", "name",
                "address", "latitude", "longitude"};

        Cursor cursor = mDatabase.query(TABLE, columns,
                null, null, null, null, null);
        if (cursor.getCount() <= 0) {
            return null;
        } else {
            cursor.moveToFirst();
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String address = cursor.getString(cursor.getColumnIndex("address"));
            double latitude = cursor.getDouble(cursor
                    .getColumnIndex("latitude"));
            double longitude = cursor.getDouble(cursor
                    .getColumnIndex("longitude"));
            LatLng latLng = new LatLng(latitude, longitude);
            place = new Place(id, name, address, latLng);

            cursor.close();
            return place;
        }
    }

    /**
     * Used for update row in place table.
     *
     * @param place
     * @return True if updated successful, false otherwise
     */
    public boolean updatePlace(Place place) {

        ContentValues cv = new ContentValues();
        cv.put("id", place.getId());
        cv.put("name", place.getName());
        LatLng loc = place.getLocation();
        cv.put("latitude", loc.latitude);
        cv.put("longitude", loc.longitude);
        cv.put("address", place.getAddress());

        String[] ids = new String[1];
        ids[0] = String.valueOf(place.getId());
        Cursor cursor = mDatabase.query(TABLE, null,
                "id=?", ids,
                null, null, null);
        if (cursor.getCount() > 0) {
            mDatabase.update(TABLE, cv, "id=?", ids);
        } else {
            if (mDatabase.insert("place", null, cv) != -1) {
                return true;
            } else {
                return false;
            }
        }

        return true;
    }

}

