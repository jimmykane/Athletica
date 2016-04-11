package com.dimitrioskanellopoulos.athletica;

import android.hardware.SensorManager;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

public class LocationEngine implements LocationListener {

    private static final String TAG = "LocationEngine";
    private static final Float MAX_ACCEPTED_ACCURACY = 50.0f;

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

    public Double getAltitude(Float pressure){
        Log.d(TAG, "Calculating Combined Pressure Altitude");
        // Get the pressure altitude from the pressure
        Float pressureAltitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure);

        if (lastKnownLocation == null
                || !lastKnownLocation.hasAltitude()
                || lastKnownLocation.getAccuracy() > MAX_ACCEPTED_ACCURACY
                || System.currentTimeMillis() - lastKnownLocation.getTime() > 600000){
            Log.d(TAG, "Returned altitude from pressure");
            return (double)pressureAltitude;
        }

        // We have a location with altitude
        Double gpsAltitude = lastKnownLocation.getAltitude();
        Log.d(TAG, "Returned combined altitude");
        Log.d(TAG, pressureAltitude.toString());
        Log.d(TAG, gpsAltitude.toString());
        Log.d(TAG, lastKnownLocation.toString());
        return (6*pressureAltitude + 2*gpsAltitude)/8;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");
    }
}