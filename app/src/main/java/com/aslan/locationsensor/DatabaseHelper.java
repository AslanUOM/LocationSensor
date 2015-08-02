package com.aslan.locationsensor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vishnuvathsasarma on 22-Jul-15.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "LocationHistory.db";

    public static final String LOCATION_TABLE_NAME = "Locations";
    public static final String LOCATION_COLUMN_TIME = "Time";
    public static final String LOCATION_COLUMN_PROVIDER = "Provider";
    public static final String LOCATION_COLUMN_LATITUDE = "Latitude";
    public static final String LOCATION_COLUMN_LONGITUDE = "Longitude";
    public static final String LOCATION_COLUMN_ALTITUDE = "Altitude";
    public static final String LOCATION_COLUMN_SPEED = "Speed";
    public static final String LOCATION_COLUMN_BEARING = "Bearing";
    public static final String LOCATION_COLUMN_ACCURACY = "Accuracy";
    public static final String WIFI_TABLE_NAME = "WiFi";
    public static final String WIFI_COLUMN_TIME = "Time";
    public static final String WIFI_COLUMN_SSID = "SSID";
    public static final String WIFI_COLUMN_BSSID = "BSSID";
    public static final String WIFI_COLUMN_CAPABILITIES = "Capabilities";
    public static final String WIFI_COLUMN_LEVEL = "Level";
    public static final String WIFI_COLUMN_FREQUENCY = "Frequency";
    //Table to store user,device registration data for NodeGrid and GCM push
    public static final String REGISTRATION_TABLE_NAME = "RegData";
    public static final String REGISTRATION_COLUMN_KEY = "Key";
    public static final String REGISTRATION_COLUMN_VALUE = "Value";
    private final String CREATE_LOCATION_TABLE = "CREATE TABLE "
            + LOCATION_TABLE_NAME
            + " ("
            + LOCATION_COLUMN_TIME
            + " text, "
            + LOCATION_COLUMN_PROVIDER
            + " text, "
            + LOCATION_COLUMN_LATITUDE
            + " text, "
            + LOCATION_COLUMN_LONGITUDE
            + " text, "
            + LOCATION_COLUMN_ALTITUDE
            + " text, "
            + LOCATION_COLUMN_SPEED
            + " text, "
            + LOCATION_COLUMN_BEARING
            + " text, "
            + LOCATION_COLUMN_ACCURACY
            + " text)";
    private final String CREATE_WIFI_TABLE = "CREATE TABLE "
            + WIFI_TABLE_NAME
            + " ("
            + WIFI_COLUMN_TIME
            + " text, "
            + WIFI_COLUMN_SSID
            + " text, "
            + WIFI_COLUMN_BSSID
            + " text, "
            + WIFI_COLUMN_CAPABILITIES
            + " text, "
            + WIFI_COLUMN_LEVEL
            + " text, "
            + WIFI_COLUMN_FREQUENCY
            + " text)";
    private final String CREATE_REGISTRATION_TABLE = "CREATE TABLE "
            + REGISTRATION_TABLE_NAME
            + " ("
            + REGISTRATION_COLUMN_KEY
            + " text, "
            + REGISTRATION_COLUMN_VALUE
            + " text)";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(CREATE_WIFI_TABLE);
        sqLiteDatabase.execSQL(CREATE_REGISTRATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + REGISTRATION_TABLE_NAME);
        sqLiteDatabase.execSQL(CREATE_REGISTRATION_TABLE);
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + LOCATION_TABLE_NAME);
//        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WIFI_TABLE_NAME);
//        onCreate(sqLiteDatabase);
    }

    public boolean insertLocation(Location location) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LOCATION_COLUMN_TIME, new Timestamp(location.getTime()).toString());
        contentValues.put(LOCATION_COLUMN_PROVIDER, location.getProvider());
        contentValues.put(LOCATION_COLUMN_LATITUDE, location.getLatitude());
        contentValues.put(LOCATION_COLUMN_LONGITUDE, location.getLongitude());
        contentValues.put(LOCATION_COLUMN_ALTITUDE, location.getAltitude());
        contentValues.put(LOCATION_COLUMN_SPEED, location.getSpeed());
        contentValues.put(LOCATION_COLUMN_BEARING, location.getBearing());
        contentValues.put(LOCATION_COLUMN_ACCURACY, location.getAccuracy());
        db.insert(LOCATION_TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public boolean insertWifi(ScanResult scanResult, String timeStamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(WIFI_COLUMN_TIME, timeStamp);
        contentValues.put(WIFI_COLUMN_SSID, scanResult.SSID);
        contentValues.put(WIFI_COLUMN_BSSID, scanResult.BSSID);
        contentValues.put(WIFI_COLUMN_CAPABILITIES, scanResult.capabilities);
        contentValues.put(WIFI_COLUMN_LEVEL, scanResult.level);
        contentValues.put(WIFI_COLUMN_FREQUENCY, scanResult.frequency);
        db.insert(WIFI_TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public boolean insertRegData(String key, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(REGISTRATION_COLUMN_KEY, key);
        contentValues.put(REGISTRATION_COLUMN_VALUE, value);
        db.insert(REGISTRATION_TABLE_NAME, null, contentValues);
        db.close();
        return true;
    }

    public String getRegData(String key) {
        String value;
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT " + REGISTRATION_COLUMN_KEY + "," + REGISTRATION_COLUMN_VALUE
                + " FROM " + REGISTRATION_TABLE_NAME
                + " WHERE " + REGISTRATION_COLUMN_KEY + "='" + key + "'", null);
        res.moveToFirst();
        try {
            value = res.getString(res.getColumnIndex(REGISTRATION_COLUMN_VALUE));
        } catch (Exception re) {
            value = null;
        }
        res.close();
        db.close();
        return value;
    }

//    public Cursor getData(int id){
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor res =  db.rawQuery( "select * from contacts where id="+id+"", null );
//        return res;
//    }

    public int getNumberOfLocationRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int rows = (int) DatabaseUtils.queryNumEntries(db, LOCATION_TABLE_NAME);
        db.close();
        return rows;
    }

    public int getNumberOfWifiRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int rows = (int) DatabaseUtils.queryNumEntries(db, WIFI_TABLE_NAME);
        db.close();
        return rows;
    }

//    public boolean updateContact (Integer id, String name, String phone, String email, String street,String place)
//    {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put("name", name);
//        contentValues.put("phone", phone);
//        contentValues.put("email", email);
//        contentValues.put("street", street);
//        contentValues.put("place", place);
//        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
//        return true;
//    }

//    public Integer deleteContact (Integer id)
//    {
//        SQLiteDatabase db = this.getWritableDatabase();
//        return db.delete("contacts",
//                "id = ? ",
//                new String[] { Integer.toString(id) });
//    }

    public List<String> getRecentLocations(int length) {
        List<String> array_list = new ArrayList<>();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + LOCATION_TABLE_NAME
                + " ORDER BY " + LOCATION_COLUMN_TIME
                + " DESC LIMIT " + length, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            array_list.add(res.getString(res.getColumnIndex(LOCATION_COLUMN_PROVIDER))
                    + ", " + res.getString(res.getColumnIndex(LOCATION_COLUMN_LATITUDE))
                    + ", " + res.getString(res.getColumnIndex(LOCATION_COLUMN_LONGITUDE))
                    + ", " + res.getString(res.getColumnIndex(LOCATION_COLUMN_TIME)));
            res.moveToNext();
        }
        res.close();
        db.close();
        return array_list;
    }

    public List<String> getRecentWifi(int length) {
        List<String> array_list = new ArrayList<>();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + WIFI_TABLE_NAME
                + " ORDER BY " + WIFI_COLUMN_TIME
                + " DESC LIMIT " + length, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            array_list.add(res.getString(res.getColumnIndex(WIFI_COLUMN_SSID)) + ", " + res.getString(res.getColumnIndex(WIFI_COLUMN_BSSID)) + ", " + res.getString(res.getColumnIndex(WIFI_COLUMN_TIME)));
            res.moveToNext();
        }
        res.close();
        db.close();
        return array_list;
    }

    public List<String> getAllLocations() {
        List<String> array_list = new ArrayList<>();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + LOCATION_TABLE_NAME, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            array_list.add(res.getString(res.getColumnIndex(LOCATION_COLUMN_PROVIDER))
                    + ", " + res.getString(res.getColumnIndex(LOCATION_COLUMN_LATITUDE))
                    + ", " + res.getString(res.getColumnIndex(LOCATION_COLUMN_LONGITUDE))
                    + ", " + res.getString(res.getColumnIndex(LOCATION_COLUMN_TIME)));
            res.moveToNext();
        }
        res.close();
        db.close();
        return array_list;
    }
    public List<String> getAllWifi() {
        List<String> array_list = new ArrayList<>();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("SELECT * FROM " + WIFI_TABLE_NAME, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            array_list.add(res.getString(res.getColumnIndex(WIFI_COLUMN_SSID)) + ", " + res.getString(res.getColumnIndex(WIFI_COLUMN_BSSID)) + ", " + res.getString(res.getColumnIndex(WIFI_COLUMN_TIME)));
            res.moveToNext();
        }
        res.close();
        db.close();
        return array_list;
    }

    public void exportToSdCard(Context ctx) {
        AsyncTask<Context, Boolean, Boolean> asyncTask = new AsyncTask<Context, Boolean, Boolean>() {
            Context ctx;

            @Override
            protected Boolean doInBackground(Context... params) {
                ctx = params[0];
                return exportToSdCard(ctx.getDatabasePath(DATABASE_NAME).toString());
//                return exportToSdCard(ctx.getDatabasePath(DATABASE_NAME).toString());
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if (aBoolean) {
                    Toast.makeText(ctx, "Exported", Toast.LENGTH_SHORT).show();
                    Log.d("Path", Environment.getExternalStorageDirectory().getPath());
                }
            }
        };
        asyncTask.execute(ctx);
    }
    public boolean exportToSdCard(String currentDBPath) {
        boolean isExported = false;
        try {
            File sd = Environment.getExternalStorageDirectory();

            if (sd.canWrite()) {
                String backupDBPath;
                File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/FYP/");
                dir.mkdirs();
                if (dir.isDirectory()) {
                    backupDBPath = Environment.getExternalStorageDirectory().getPath() + "/FYP/location_db_dump_" + new Timestamp(System.currentTimeMillis()).toString() + ".db";
                } else {
                    backupDBPath = Environment.getExternalStorageDirectory().getPath() + "/location_db_dump_" + new Timestamp(System.currentTimeMillis()).toString() + ".db";
                }
                File currentDB = new File(currentDBPath);
                File backupDB = new File(backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    isExported = true;
                    src.close();
                    dst.close();
                }
            }
        } catch (Exception e) {
            isExported = false;
        }
        return isExported;
    }
}
