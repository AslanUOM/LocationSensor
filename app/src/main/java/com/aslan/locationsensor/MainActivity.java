package com.aslan.locationsensor;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;


public class MainActivity extends Activity {

    private final String MIN_DISTANCE_CHANGE = "distance";
    private final String TIME_INTERVAL = "time";
    private DatabaseHelper dbHelper;
    // The minimum distance to change location Updates in meters
    private long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10L;
    // The minimum time between location updates in milliseconds
    private long MIN_TIME_BW_UPDATES = 1000L;

    private Intent serviceIntent;

    private TextView txtLocation;
    private TextView txtWifi;
    private ListView lvLocation;
    private ListView lvWifi;
    private Button btnStart;
    private Button btnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        List<String> location_list = dbHelper.getAllLocations();
        List<String> wifi_list = dbHelper.getAllWifi();
        ArrayAdapter location_arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, location_list);
        ArrayAdapter wifi_arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, wifi_list);
        lvLocation = (ListView) findViewById(R.id.lvLocation);
        lvLocation.setAdapter(location_arrayAdapter);
        lvWifi = (ListView) findViewById(R.id.lvWifi);
        lvWifi.setAdapter(wifi_arrayAdapter);

        txtLocation = (TextView) findViewById(R.id.txtLocation);
        txtLocation.setText("Rows: " + dbHelper.getNumberOfLocationRows());
        txtWifi = (TextView) findViewById(R.id.txtWifi);
        txtWifi.setText("Rows: " + dbHelper.getNumberOfWifiRows());

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceIntent = new Intent(MainActivity.this, LocationTrackingService.class);
                serviceIntent.putExtra(MIN_DISTANCE_CHANGE, MIN_DISTANCE_CHANGE_FOR_UPDATES);
                serviceIntent.putExtra(TIME_INTERVAL, MIN_TIME_BW_UPDATES);
                serviceIntent.addCategory(LocationTrackingService.TAG);
                startService(serviceIntent);
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
            }
        });
        btnStop = (Button) findViewById(R.id.btnStop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isMyServiceRunning()) {
                    serviceIntent = new Intent(MainActivity.this, LocationTrackingService.class);
                    serviceIntent.addCategory(LocationTrackingService.TAG);
                    stopService(serviceIntent);
                    btnStart.setEnabled(true);
                    btnStop.setEnabled(false);
                }
            }
        });

        if (LocationTrackingService.isIntentServiceRunning) {
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.aslan.locationsensor.LocationTrackingService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
