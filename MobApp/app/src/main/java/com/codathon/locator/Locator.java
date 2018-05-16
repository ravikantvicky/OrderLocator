package com.codathon.locator;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.Tag;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Locator extends AppCompatActivity {
    private String requestUrl = "https://ordlocator.herokuapp.com", orderId = "ORD0001";
    private LocationManager mLocationManager;
    private TextView locationBox, latt, longt, updt, currHost;
    private Button saveBtn;
    private EditText ordId, hostName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_locator);

        locationBox = findViewById(R.id.locationBox);
        latt = findViewById(R.id.latt);
        longt = findViewById(R.id.longt);
        updt = findViewById(R.id.upd);
        saveBtn = findViewById(R.id.saveBtn);
        ordId = findViewById(R.id.orderId);
        hostName = findViewById(R.id.host);
        currHost = findViewById(R.id.currHost);

        locationBox.setText("Order Id: "+orderId);
        currHost.setText(requestUrl);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ordId.getText() != null && !ordId.getText().toString().isEmpty()) {
                    orderId = ordId.getText().toString();
                    ordId.setText("");
                    locationBox.setText("Order Id: "+orderId);
                }
                if (hostName.getText() != null && !hostName.getText().toString().isEmpty()) {
                    requestUrl = hostName.getText().toString();
                    hostName.setText("");
                    currHost.setText(requestUrl);
                }
            }
        });

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
            JSONObject locReq = new JSONObject();
            try {
                locReq.put("orderId", orderId);
                locReq.put("lattitude", location.getLatitude()+"");
                locReq.put("longitude", location.getLongitude()+"");
                sendLocationData(locReq);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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

    public void sendLocationData(final JSONObject locReq) {
        Log.i("Service call Start", "url: "+requestUrl+"/saveLocData");
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(
                Request.Method.POST, requestUrl+"/saveLocData", locReq,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Success Response: ", response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getBaseContext(), "Unable to make service call.", Toast.LENGTH_LONG);
                Log.i("Error Response: ", "Error: " + error.getMessage());
            }
        }) {

            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

        };

        // Adding request to request queue
        Volley.newRequestQueue(this).add(jsonObjReq);
    }
}
