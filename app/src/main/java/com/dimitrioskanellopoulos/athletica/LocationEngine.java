package com.dimitrioskanellopoulos.athletica;

import android.hardware.SensorManager;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

public class LocationEngine implements LocationListener {

    private static final String TAG = "LocationEngine";
    private static final Float MAX_ACCEPTED_ACCURACY =200.0f;
    private static final Integer LAST_KNOWN_LOCATION_AGE = 3600000; // 1 hour
    private static final Float ACCURACY_WEIGHT = 0.3f;
    private static final Float AGE_WEIGHT = 0.7f;

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

    public Float getAltitudeFromPressure(Float pressure){
        Log.d(TAG, "Calculating altitude from pressure");
        return SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");
    }
}