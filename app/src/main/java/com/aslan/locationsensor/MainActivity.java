package com.aslan.locationsensor;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;


public class MainActivity extends Activity {

    private TextView txtLocation;
    private LocationReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtLocation = (TextView) findViewById(R.id.txtLocation);
        receiver = new LocationReceiver(this, 10, 1000);
        receiver.setOnLocationChangedListener(new OnLocationChangedListener() {

            @Override
            public void onLocationChanged(Location location) {
                // Do whatever you want here
                txtLocation.append("Lat: " + location.getLatitude() + "\nLon: "
                        + location.getLongitude() + "\n");
            }
        });
        receiver.start();
    }

    @Override
    protected void onPause() {
        receiver.stop();
        super.onPause();
    }
}
