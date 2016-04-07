package com.dimitrioskanellopoulos.activityface;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

public class LocationEngine implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "LocationEngine";

    private GoogleApiClient googleApiClient;

    private Location lastKnownLocation;

    public LocationEngine(Context context) {
        googleApiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();;
    }


    @Override
    public void onConnected(Bundle connectionHint) {
        Log.e(TAG, "Connected!");
        // Provides a simple way of getting a device's location and is well suited for
        // applications that do not require a fine-grained location and that do not need location
        // updates. Gets the best and most recent location currently available, which may be null
        // in rare cases when a location is not available.
        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (lastKnownLocation != null) {
            Log.e(TAG, "!!!!Location found");
            return;
        }
    }

    public Location get(){
        // Try once more to get the loc
        if (this.googleApiClient != null && this.googleApiClient.isConnected()){
            this.lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }
        return this.lastKnownLocation;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "suspended GoogleAPI");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "connectionFailed GoogleAPI");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "Location changed");
    }

}