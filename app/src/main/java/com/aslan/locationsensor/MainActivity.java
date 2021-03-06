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
    //no of rows to load from sqlite
    private final int MAX_ROW_SIZE = 10;
    private final String KEY_NAME = "name";
    private final String VALUE_NAME = "Vishnu";
    private final String KEY_USERNAME = "username";
    private final String VALUE_USERNAME = "vishnu24";
    private final String KEY_PWD = "password";
    private final String VALUE_PWD = "1234";
    private DatabaseHelper dbHelper;
    //    private final String KEY_ID_USER = "_id";
//    private final String KEY_TOKEN_ACCESS = "accessToken";
//    private final String KEY_RELATION_HAS = "has";
//    private final String KEY_DEVICE = "name";
//    private final String KEY_ID_DEVICE = "dname";
//    private final String KEY_DEVICE_NOTIFIER = "notifier";
//    private final String VALUE_DEVICE_NOTIFIER = "google";
//    private final String KEY_TOKEN_DEVICE = "regId";
    private Intent serviceIntent;
    private TextView txtLocation;
    private TextView txtWifi;
    private ListView lvLocation;
    private ListView lvWifi;
    private Button btnStart;
    private Button btnStop;
    private Button btnExportToSD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        if (dbHelper.getRegData(KEY_NAME) == null) {
            Intent configInten = new Intent(MainActivity.this, ConfigActivity.class);
            configInten.putExtra("isFresh", true);
            startActivity(configInten);
        }
        List<String> location_list = dbHelper.getRecentLocations(MAX_ROW_SIZE);
        List<String> wifi_list = dbHelper.getRecentWifi(MAX_ROW_SIZE);

        ArrayAdapter<String> location_arrayAdapter;
        location_arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, location_list);
        ArrayAdapter<String> wifi_arrayAdapter;
        wifi_arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wifi_list);

        lvLocation = (ListView) findViewById(R.id.lvLocation);
        lvLocation.setAdapter(location_arrayAdapter);
        lvWifi = (ListView) findViewById(R.id.lvWifi);
        lvWifi.setAdapter(wifi_arrayAdapter);

        txtLocation = (TextView) findViewById(R.id.txtLocation);
        txtLocation.setText("Total Rows: " + dbHelper.getNumberOfLocationRows());
        txtWifi = (TextView) findViewById(R.id.txtWifi);
        txtWifi.setText("Total Rows: " + dbHelper.getNumberOfWifiRows());

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceIntent = new Intent(MainActivity.this, LocationTrackingService.class);
                serviceIntent.addCategory(LocationTrackingService.TAG);
                startService(serviceIntent);
                btnStart.setEnabled(false);
                btnStop.setEnabled(true);
                btnExportToSD.setEnabled(false);
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
                    btnExportToSD.setEnabled(true);
                }
            }
        });
        btnExportToSD = (Button) findViewById(R.id.btnExportToSD);
        btnExportToSD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.exportToSdCard(getApplicationContext());
            }
        });

        if (isMyServiceRunning()) {
            btnStart.setEnabled(false);
            btnStop.setEnabled(true);
            btnExportToSD.setEnabled(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        List<String> location_list = dbHelper.getRecentLocations(MAX_ROW_SIZE);
        List<String> wifi_list = dbHelper.getRecentWifi(MAX_ROW_SIZE);

        ArrayAdapter<String> location_arrayAdapter;
        location_arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, location_list);
        ArrayAdapter<String> wifi_arrayAdapter;
        wifi_arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, wifi_list);

        lvLocation.setAdapter(location_arrayAdapter);
        lvWifi.setAdapter(wifi_arrayAdapter);

        txtLocation.setText("Total Rows: " + dbHelper.getNumberOfLocationRows());
        txtWifi.setText("Total Rows: " + dbHelper.getNumberOfWifiRows());

        super.onResume();
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
