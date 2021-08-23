package database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DBHelper extends SQLiteOpenHelper {

    private static String TAG = "DBHelper";

    private static final String DATABASE_NAME = "pic.db";

    private static final int DATABASE_VERSION = 1;

    public SQLiteDatabase db;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = super.getWritableDatabase();
    }

    public Cursor rawQuery(String sql, String[] selectionArgs) {
        return db.rawQuery(sql, selectionArgs);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {

            String sql = "CREATE TABLE contacts (\n" +
                    "    phoneNumber                      VARCHAR  PRIMARY KEY NOT NULL,\n" +
                    "    name             VARCHAR NOT NULL,\n" +
                    "    bool             INTEGER NOT NULL\n" +

            ");\n";

            db.execSQL(sql);
        } catch (Exception e) {
            Log.e(TAG, "DB Create Failed", e);
            throw e;
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {


    }
}

