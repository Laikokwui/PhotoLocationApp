package com.example.phototomap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CAMERA = 1;
    public static final String CURRENT_LATITUDE_KEY = "latitude";
    public static final String CURRENT_LONGITUDE_KEY = "longitude";
    public static final String INTENT_ACTION = "currentLocation";

    private ImageView imageView_photo;
    private TextView textView_address;
    private FragmentMaps fragmentMaps;
    private DatabaseImage db;
    private ArrayList<MyLocation> location_list;
    private double current_Latitude = 0.0, current_Longitude = 0.0;

    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(INTENT_ACTION)) {
                current_Latitude = intent.getDoubleExtra(CURRENT_LATITUDE_KEY,0.0);
                current_Longitude = intent.getDoubleExtra(CURRENT_LONGITUDE_KEY,0.0);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseImage(this, 1);
        getAllLocationsFromDB();
        imageView_photo = findViewById(R.id.imageView_photo);
        textView_address = findViewById(R.id.textView_address);
        FloatingActionButton floatingActionButton_camera = findViewById(R.id.floatingActionButton_camera);

        floatingActionButton_camera.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("QueryPermissionsNeeded")
            @Override
            public void onClick(View v) {
                JobScheduler jobScheduler = (JobScheduler)getApplicationContext().getSystemService(JOB_SCHEDULER_SERVICE);
                JobInfo jobInfo = new JobInfo.Builder(0, new ComponentName(getApplicationContext(),LocationTracker.class)).setMinimumLatency(0).build();
                jobScheduler.schedule(jobInfo);

                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, REQUEST_CAMERA);
                }
            }
        });

        fragmentMaps = new FragmentMaps(location_list);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment,fragmentMaps).commit();
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, new IntentFilter(INTENT_ACTION));
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                getAddress(current_Latitude, current_Longitude);
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                imageView_photo.setImageBitmap(bitmap);

                MyLocation duplicatedLocation = locationExist(current_Latitude,current_Longitude);
                if (duplicatedLocation == null) {
                    db.AddALocation(new MyLocation(current_Latitude, current_Longitude, bitmap));
                }
                else {
                    duplicatedLocation.setBitmap(bitmap);
                    db.UpdateLocation(duplicatedLocation);
                }

                getAllLocationsFromDB();
                fragmentMaps.refreshMarkers(location_list);
            }
        }
    }

    private MyLocation locationExist(double currentLat, double currentLon) {
        for (MyLocation myLocation : location_list) {
            if (currentLat == myLocation.getLatitude() && currentLon == myLocation.getLongitude()) { return myLocation; }
        }
        return null;
    }

    private void getAllLocationsFromDB() {
        if (location_list != null) { location_list.clear(); }
        location_list = db.getAllLocations();
    }

    private void getAddress(double latitude, double longitude) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?sensor=true&latlng="
                + latitude
                + ","
                + longitude
                + "&key="
                + getString(R.string.google_maps_key);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("results");
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String address = jsonObject.getString("formatted_address");
                    textView_address.setText(address);
                } catch (JSONException e) {
                    textView_address.setText(R.string.error_failed_to_get_address);
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textView_address.setText(R.string.error_no_address);
            }
        });

        final RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);
    }

    public void displayMarkerDetail(double latitude, double longitude, Bitmap bitmap) {
        getAddress(latitude,longitude);
        imageView_photo.setImageBitmap(bitmap);
    }
}
