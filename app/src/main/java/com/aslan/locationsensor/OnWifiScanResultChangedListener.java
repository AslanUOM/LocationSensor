package com.aslan.locationsensor;

import android.net.wifi.ScanResult;

import java.util.List;

/**
 * Created by Vishnuvathsasarma on 21-Jul-15.
 */
public interface OnWifiScanResultChangedListener {
    void onWifiScanResultsChanged(List<ScanResult> wifiList);
}
