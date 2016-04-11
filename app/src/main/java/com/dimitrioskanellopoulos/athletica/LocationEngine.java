package com.dimitrioskanellopoulos.athletica;

import android.hardware.SensorManager;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

public class LocationEngine implements LocationListener {

    private static final String TAG = "LocationEngine";
    private static final Float MAX_ACCEPTED_ACCURACY = 50.0f;
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

    public Double getAltitude(Float pressure){
        Log.d(TAG, "Calculating Combined Pressure Altitude");
        // Get the pressure altitude from the pressure
        Float pressureAltitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure);

        if (lastKnownLocation == null
                || !lastKnownLocation.hasAltitude()
                || lastKnownLocation.getAccuracy() > MAX_ACCEPTED_ACCURACY
                || (System.currentTimeMillis() - lastKnownLocation.getTime()) > LAST_KNOWN_LOCATION_AGE){
            Log.d(TAG, "Returning altitude from pressure");
            return (double)pressureAltitude;
        }

        // We have a location with altitude
        Double gpsAltitude = lastKnownLocation.getAltitude();
        // @todo check algo
        Float gpsAltitudeWeight =
                (lastKnownLocation.getAccuracy()*ACCURACY_WEIGHT + lastKnownLocation.getTime()*AGE_WEIGHT)
                / (lastKnownLocation.getAccuracy() + lastKnownLocation.getTime());

        Double combinedAltitude = (pressureAltitude + gpsAltitude*gpsAltitudeWeight)/(2 - gpsAltitudeWeight);

        Log.d(TAG, "Returning combined altitude");
        return combinedAltitude;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");
    }
}