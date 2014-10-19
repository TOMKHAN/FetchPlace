package hr.foi.tosulc.fetchplace.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tosulc on 15.10.2014.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "database.db";
    private static final int DATABASE_VERSION = 1;

    private Context mContext;

    public DBHelper(Context c) {
        super(c, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = c;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE place (id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name VARCHAR(50),address VARCHAR(50), latitude REAL, longitude REAL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
