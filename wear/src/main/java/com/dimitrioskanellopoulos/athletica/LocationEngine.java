package com.dimitrioskanellopoulos.athletica;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

public class LocationEngine implements LocationListener {

    private static final String TAG = "LocationEngine";

    private GoogleApiHelper googleApiHelper;

    private Location lastKnownLocation;

    private Context context;

    public LocationEngine(Context context, GoogleApiHelper googleApiHelper) {
        this.googleApiHelper = googleApiHelper;
        this.context = context;
    }

    public Location getLastKnownLocation() {
        if (googleApiHelper.isConnected()) {
            lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiHelper.getGoogleApiClient());
        }
        return lastKnownLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");
    }
}