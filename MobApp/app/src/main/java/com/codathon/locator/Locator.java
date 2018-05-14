package com.codathon.locator;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Locator extends AppCompatActivity {
    private LocationManager mLocationManager;
    private TextView locationBox, latt, longt, updt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locator);

        locationBox = findViewById(R.id.locationBox);
        latt = findViewById(R.id.latt);
        longt = findViewById(R.id.longt);
        updt = findViewById(R.id.upd);


        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Toast.makeText(this, "Location permission not granted", Toast.LENGTH_LONG);
            return;
        }
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                10, mLocationListener);

    }
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            Calendar now = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM YYYY HH:mm:ss");
            latt.setText(location.getLatitude()+"");
            longt.setText(location.getLongitude()+"");
            updt.setText(sdf.format(now.getTime()));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Calendar now = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM YYYY HH:mm:ss");
            updt.setText(sdf.format(now.getTime()));
            locationBox.setText("Status changed. Status: "+status+", Provider: "+provider);
            Toast.makeText(getApplicationContext(), "Status changed. Status: "+status+", Provider: "+provider, Toast.LENGTH_LONG);
        }

        @Override
        public void onProviderEnabled(String provider) {
            locationBox.setText(provider+" Enabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            locationBox.setText(provider+" Disabled");
        }
    };
}
