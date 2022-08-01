package com.example.phototomap;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class LocationTracker extends JobService implements LocationListener {
    private static final String TAG = LocationTracker.class.getName();
    protected LocationManager locationManager;

    public LocationTracker() {}

    @Override
    public boolean onStartJob(JobParameters params) {
        initLocationManager();

        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0, this);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG,"Fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "Network provider does not exist, " + ex.getMessage());
        }

        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, this);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG,"fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist, " + ex.getMessage());
        }
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        locationManager.removeUpdates(this);
        return false;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Intent intent = new Intent(MainActivity.INTENT_ACTION);
        intent.setAction(MainActivity.INTENT_ACTION);
        intent.putExtra(MainActivity.CURRENT_LATITUDE_KEY,location.getLatitude());
        intent.putExtra(MainActivity.CURRENT_LONGITUDE_KEY,location.getLongitude());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) { }

    @Override
    public void onProviderDisabled(@NonNull String provider) { }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    private void initLocationManager() {
        if (locationManager == null) {
            locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
}
