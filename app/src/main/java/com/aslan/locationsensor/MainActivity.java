package com.aslan.locationsensor;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    String ITEM_KEY = "SSID";
    List<String> wifiList = new ArrayList<>();
    ArrayAdapter adapter;
    private TextView txtLocation;
    private TextView txtWifi;
    private LocationReceiver locationReceiver;
    private WifiReceiver wifiReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtLocation = (TextView) findViewById(R.id.txtLocation);
        locationReceiver = new LocationReceiver(this, 10, 1000);
        locationReceiver.setOnLocationChangedListener(new OnLocationChangedListener() {

            @Override
            public void onLocationChanged(Location location) {
                // Do whatever you want here
                txtLocation.append("Lat: " + location.getLatitude() + "\nLon: "
                        + location.getLongitude() + "\n");
            }
        });
        locationReceiver.start();

        txtWifi = (TextView) findViewById(R.id.txtWifi);
        wifiReceiver = new WifiReceiver(this);
        wifiReceiver.setOnWifiScanResultChangedLsitener(new OnWifiScanResultChangedListener() {
            @Override
            public void onWifiScanResultsChanged(List<String> wifiList) {
                MainActivity.this.wifiList = wifiList;
                for (String wifi : wifiList) {
                    txtWifi.append(wifi + "\n");
                }
//                adapter.notifyDataSetChanged();
            }
        });
        wifiReceiver.start();

//        adapter = new SimpleAdapter(this, wifiList, R.layout.row, new String[] { ITEM_KEY }, new int[] { R.id.list_value });
//        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, wifiList);
//        lvWifi.setAdapter(this.adapter);
    }

    @Override
    protected void onPause() {
        locationReceiver.stop();
        wifiReceiver.stop();
        super.onPause();
    }
}
