package com.aslan.locationsensor;

/**
 * Created by Vishnuvathsasarma on 20-Jul-15.
 */

import android.app.Service;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class LocationReceiver {

    private final Context mContext;
    // The minimum distance to change Updates in meters
    private final long MIN_DISTANCE_CHANGE_FOR_UPDATES;
    // The minimum time between updates in milliseconds
    private final long MIN_TIME_BW_UPDATES;
    // flag for GPS status
    boolean isGPSEnabled = false;
    // flag for network status
    boolean isNetworkEnabled = false;
    // flag for GPS status
    boolean canGetLocation = false;
    // Declaring a Location Manager
    private LocationManager locationManager;
    private OnLocationChangedListener listener;
    private LocationListener locationListener = new LocationListener() {

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onLocationChanged(Location location) {
            if (listener != null) {
                listener.onLocationChanged(location);
            }
        }
    };

    public LocationReceiver(Context context, long minDistanceChange,
                            long intervalInMillis) {
        this.mContext = context;
        this.MIN_DISTANCE_CHANGE_FOR_UPDATES = minDistanceChange;
        this.MIN_TIME_BW_UPDATES = intervalInMillis;
    }

    public void start() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(Service.LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
                Log.e("No Location", "Disabled");
            } else {
                this.canGetLocation = true;
                // First get location from Network Provider
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                    Log.d("Network", "Network");
                    if (locationManager != null && listener != null) {
                        Location location = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            listener.onLocationChanged(location);
                        }

                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener);
                    Log.d("GPS Enabled", "GPS Enabled");
                    if (locationManager != null && listener != null) {
                        Location location = locationManager
                                .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (location != null) {
                            listener.onLocationChanged(location);
                        }
                    }

                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    public void setOnLocationChangedListener(OnLocationChangedListener listener) {
        this.listener = listener;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }
}
