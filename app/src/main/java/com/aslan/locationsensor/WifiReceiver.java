package com.aslan.locationsensor;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import java.util.ArrayList;
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

    //    String ITEM_KEY = "SSID";
    List<String> wifiList = new ArrayList<>();

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
//                List<ScanResult> mItems = new ArrayList<ScanResult>();
//                results = wifiManager.getScanResults();
//                size = results.size();
//                HashMap<String, Integer> signalStrength = new HashMap<String, Integer>();
//                try {
//                    for (int i = 0; i < size; i++) {
//                        ScanResult result = results.get(i);
//                        if (!result.SSID.isEmpty()) {
//                            String key = result.SSID + " "
//                                    + result.capabilities;
//                            Log.i("TAG", "ssid:" + result.SSID + "level:"
//                                    + result.level);
//                            if (!signalStrength.containsKey(key)) {
//                                signalStrength.put(key, i);
//                                mItems.add(result);
////                                adapter.notifyDataSetChanged();
//                                listener.onWifiScanResultsChanged(mItems);
//                            } else {
//                                int position = signalStrength.get(key);
//                                ScanResult updateItem = mItems.get(position);
//                                if (calculateSignalStength(wifiManager, updateItem.level) >
//                                        calculateSignalStength(wifiManager, result.level)) {
//                                    mItems.set(position, updateItem);
////                                    adapter.notifyDataSetChanged();
//
//                                }
//                            }
//                        }
//                    }
//                } catch (Exception e) {
//                }
                }
            }
        };
        mContext.registerReceiver(broadcastReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wifiList.clear();
        wifiManager.startScan();
    }

    public void onResult() {
        Toast.makeText(mContext, "Scanning...." + size, Toast.LENGTH_SHORT).show();
        try {
            if (size == 0) {
                Toast.makeText(mContext.getApplicationContext(), "something wrong", Toast.LENGTH_LONG).show();
            } else {
                wifiList.clear();
                int i = size - 1;
                while (i >= 0) {
//                    HashMap<String, String> item = new HashMap<String, String>();
//                    item.put(ITEM_KEY, results.get(i).SSID + "  " + results.get(i).capabilities);

//                    String res = results.get(i).SSID + "\n"
//                            + results.get(i).capabilities + "\n"
//                            + results.get(i).level + "\n"
//                            + results.get(i).BSSID + "\n"
//                            + results.get(i).frequency + "\n"
//                            + results.get(i).toString() + "\n"
//                            ;
//                    wifiList.add(res);
                    wifiList.add(results.get(i).toString() + "\n");
                    i--;
                    listener.onWifiScanResultsChanged(wifiList);
                    //adapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
            Toast.makeText(mContext.getApplicationContext(), "exception ", Toast.LENGTH_LONG).show();
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
