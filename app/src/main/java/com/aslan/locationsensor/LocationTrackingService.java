package com.aslan.locationsensor;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.net.wifi.ScanResult;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by Vishnuvathsasarma on 21-Jul-15.
 */
public class LocationTrackingService extends IntentService {

    public static final String TAG = "MyServiceTag";
    public static boolean isIntentServiceRunning = false;
    private final String MIN_DISTANCE_CHANGE = "distance";
    private final String TIME_INTERVAL = "time";
    // The minimum distance to change location Updates in meters
    private final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10L;
    // The minimum time between location updates in milliseconds
    private final long MIN_TIME_BW_UPDATES = 1800000L;
    private DatabaseHelper dbHelper;
    private LocationReceiver locationReceiver;
    private WifiReceiver wifiReceiver;

    private Intent intent;

    public LocationTrackingService() {
        super("LocationTrackingService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        dbHelper = new DatabaseHelper(this);

        startLocationTracking();
        startWifiTracking();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        onHandleIntent(intent);
        locationReceiver.start();
        wifiReceiver.start();
        Log.d("<<Tracking-onStart>>", "I am alive");
        Toast.makeText(getApplicationContext(), "STARTED", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        onHandleIntent(intent);
//        locationReceiver.start();
//        wifiReceiver.start();
//        Log.d("<<Tracking-onStart>>", "I am alive");
//        Toast.makeText(getApplicationContext(), "STARTED", Toast.LENGTH_SHORT).show();
        super.onStartCommand(intent, START_STICKY, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        isIntentServiceRunning = false;
        locationReceiver.stop();
        wifiReceiver.stop();
        stopSelf();
        Log.e("<<Tracking-onDestroy>>", "I am DESTROYED");
        Toast.makeText(getApplicationContext(), "DESTROYED", Toast.LENGTH_SHORT).show();

        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isIntentServiceRunning) {
            isIntentServiceRunning = true;
        }
        this.intent = intent;
    }

    private void startLocationTracking() {
        locationReceiver = new LocationReceiver(this, MIN_DISTANCE_CHANGE_FOR_UPDATES, MIN_TIME_BW_UPDATES);
        locationReceiver.setOnLocationChangedListener(new OnLocationChangedListener() {

            @Override
            public void onLocationChanged(Location location) {
                if (dbHelper.insertLocation(location)) {
                    String loc = "" + location.toString();
                    Log.d("LOCATION", loc);
//                    Toast.makeText(getApplicationContext(), loc, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startWifiTracking() {
        wifiReceiver = new WifiReceiver(this);
        wifiReceiver.setOnWifiScanResultChangedLsitener(new OnWifiScanResultChangedListener() {
            @Override
            public void onWifiScanResultsChanged(List<ScanResult> wifiList) {
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                Log.e("TIME", timestamp.toString());
                for (ScanResult wifi : wifiList) {
                    if (dbHelper.insertWifi(wifi, timestamp.toString())) {
                        Log.d("WIFI", wifi.toString());
//                        Toast.makeText(getApplicationContext(), wifi.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}