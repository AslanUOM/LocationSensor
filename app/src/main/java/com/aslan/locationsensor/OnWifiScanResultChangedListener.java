package com.aslan.locationsensor;

import java.util.List;

/**
 * Created by Vishnuvathsasarma on 21-Jul-15.
 */
public interface OnWifiScanResultChangedListener {
    void onWifiScanResultsChanged(List<String> wifiList);
}
