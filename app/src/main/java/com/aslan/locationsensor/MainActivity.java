package com.aslan.locationsensor;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    List<String> wifiList = new ArrayList<>();

    private Intent serviceIntent;

    private TextView txtLocation;
    private TextView txtWifi;
    private Button btnStart;
    private Button btnStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtLocation = (TextView) findViewById(R.id.txtLocation);
        txtWifi = (TextView) findViewById(R.id.txtWifi);
        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serviceIntent = new Intent(MainActivity.this, LocationTrackingService.class);
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
