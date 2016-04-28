package com.dimitrioskanellopoulos.athletica;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

public class LocationEngine implements LocationListener {

    private static final String TAG = "LocationEngine";

    private GoogleApiHelper googleApiHelper;

    private Location lastKnownLocation;

    private LocationManager locationManager;

    public LocationEngine(Context context, GoogleApiHelper googleApiHelper) {
        this.googleApiHelper = googleApiHelper;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public Location getLastKnownLocation() {
        if (googleApiHelper.isConnected()) {
            //locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
            lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiHelper.getGoogleApiClient());
        }
        return lastKnownLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");
    }
}