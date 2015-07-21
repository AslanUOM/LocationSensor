package com.aslan.locationsensor;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

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
    private long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10L;
    // The minimum time between location updates in milliseconds
    private long MIN_TIME_BW_UPDATES = 1000L;
    private LocationReceiver locationReceiver;
    private WifiReceiver wifiReceiver;

    private Intent intent;

    public LocationTrackingService() {
        super("LocationTrackingService");
    }

    public LocationTrackingService(String name) {
        super(name);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startLocationTracking();
        startWifiTracking();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        onHandleIntent(intent);
        locationReceiver.start();
        wifiReceiver.start();
        Log.e("<<Tracking-onStart>>", "I am alive");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onHandleIntent(intent);
        locationReceiver.start();
        wifiReceiver.start();
        Log.e("<<Tracking-onStart>>", "I am alive");

        return super.onStartCommand(intent, START_STICKY, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isIntentServiceRunning = false;
        stopSelf();
        locationReceiver.stop();
        wifiReceiver.stop();
        Log.e("<<Tracking-onDestroy>>", "I am DESTROYED");
        Toast.makeText(getApplicationContext(), "DESTROYED", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isIntentServiceRunning) {
            isIntentServiceRunning = true;
        }
        this.intent = intent;
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            MIN_DISTANCE_CHANGE_FOR_UPDATES = bundle.getLong(MIN_DISTANCE_CHANGE);
            MIN_TIME_BW_UPDATES = bundle.getLong(TIME_INTERVAL);
        }
    }

    private void startLocationTracking() {
        locationReceiver = new LocationReceiver(this, MIN_DISTANCE_CHANGE_FOR_UPDATES, MIN_TIME_BW_UPDATES);
        locationReceiver.setOnLocationChangedListener(new OnLocationChangedListener() {

            @Override
            public void onLocationChanged(Location location) {
                // Do whatever you want here
                Log.d("LOCATION", "Lat: " + location.getLatitude() + ", Lon: "
                        + location.getLongitude());
                Toast.makeText(getApplicationContext(), "Lat: " + location.getLatitude() + "\nLon: "
                        + location.getLongitude(), Toast.LENGTH_SHORT).show();
//                    txtLocation.append("Lat: " + location.getLatitude() + "\nLon: "
//                            + location.getLongitude() + "\n");
            }
        });
    }

    private void startWifiTracking() {
        wifiReceiver = new WifiReceiver(this);
        wifiReceiver.setOnWifiScanResultChangedLsitener(new OnWifiScanResultChangedListener() {
            @Override
            public void onWifiScanResultsChanged(List<String> wifiList) {
                for (String wifi : wifiList) {
                    Log.d("WIFI", wifi);
                    Toast.makeText(getApplicationContext(), wifi, Toast.LENGTH_SHORT).show();
//                        txtWifi.append(wifi + "\n");
                }
//                adapter.notifyDataSetChanged();
            }
        });
    }
}
