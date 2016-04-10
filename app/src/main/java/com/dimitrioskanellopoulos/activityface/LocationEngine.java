package com.dimitrioskanellopoulos.activityface;

import android.hardware.SensorManager;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

public class LocationEngine implements LocationListener {

    private static final String TAG = "LocationEngine";

    private GoogleApiHelper googleApiHelper;

    private Location lastKnownLocation;

    public LocationEngine(GoogleApiHelper googleApiHelper) {
        this.googleApiHelper = googleApiHelper;
    }

    public Location getLastKnownLocation(){
        if (googleApiHelper.isConnected()){
            lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiHelper.getGoogleApiClient());
        }
        return lastKnownLocation;
    }

    public Double calculatePressureCombinedAltitude(Float pressure){
        Log.d(TAG, "Calculating Combined Pressure Altitude");
        // Get the pressure altitude from the pressure
        Float pressureAltitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure);
        // Get the latest location or return the altitude from pressure if no location
        lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiHelper.getGoogleApiClient());
        if (lastKnownLocation == null || !lastKnownLocation.hasAltitude()){
            Log.d(TAG, "Returned altitude from pressure");
            return (double)pressureAltitude;
        }
        // We have a location
        Double gpsAltitude = lastKnownLocation.getAltitude();
        // If the location is back in the past return the pressure one
        if (System.currentTimeMillis() - lastKnownLocation.getTime() > 600000) { // 10 minutes
            Log.d(TAG, "Returned altitude from pressure");
            return (double)pressureAltitude;
        }

        Log.d(TAG, "Returned combined altitude");
        Log.d(TAG, pressureAltitude.toString());
        Log.d(TAG, gpsAltitude.toString());
        Log.d(TAG, lastKnownLocation.toString());
        return (pressureAltitude + gpsAltitude)/2;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");
    }
}