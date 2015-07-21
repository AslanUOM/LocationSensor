package com.aslan.locationsensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Vishnuvathsasarma on 21-Jul-15.
 */
public class WifiReceiver {
    Context mContext;
    OnWifiScanResultChangedListener listener;
    WifiManager wifiManager;
    BroadcastReceiver broadcastReceiver;
    int size = 0;
    List<ScanResult> results;

    public WifiReceiver(Context context) {
        mContext = context;
    }

    public void start() {
        wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(mContext.getApplicationContext(), "wifi is disabled..making it enabled", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                results = wifiManager.getScanResults();
                if (results != null) {
                    size = results.size();
                    onResult();
                }
            }
        };
        mContext.registerReceiver(broadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
    }

    public void onResult() {
        Toast.makeText(mContext, "Scanning.... " + size, Toast.LENGTH_SHORT).show();
        if (size == 0) {
            Toast.makeText(mContext.getApplicationContext(), "NOTHING FOUND", Toast.LENGTH_LONG).show();
            Log.d("WIFI", "NOTHING FOUND");
        } else {
            listener.onWifiScanResultsChanged(results);
        }
    }

    public void stop() {
        mContext.unregisterReceiver(broadcastReceiver);
    }

    public void setOnWifiScanResultChangedLsitener(OnWifiScanResultChangedListener listener) {
        this.listener = listener;
    }

//    public static int calculateSignalStength(WifiManager wifiManager, int level){
//        return wifiManager.calculateSignalLevel(level, 5) + 1;
//    }
}
