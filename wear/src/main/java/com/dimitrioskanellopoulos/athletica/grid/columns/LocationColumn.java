package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Pair;

import com.dimitrioskanellopoulos.athletica.helpers.EmulatorHelper;
import com.dimitrioskanellopoulos.athletica.helpers.SunriseSunsetHelper;
import com.dimitrioskanellopoulos.athletica.permissions.PermissionsHelper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.Wearable;

import java.util.Calendar;
import java.util.TimeZone;

public abstract class LocationColumn extends Column implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private final static String TAG = "LocationColumn";
    /**
     * The location update intervals: 1hour in ms
     */
    private static final long LOCATION_UPDATE_INTERVAL_MS = 3600000;
    private static final long LOCATION_UPDATE_FASTEST_INTERVAL_MS = 3600000;

    /**
     * A helper for google api that can be shared within the app
     */
    private static GoogleApiClient googleApiClient;

    private PermissionsHelper permissionsHelper;

    /**
     * The location request we will be making
     */
    private final LocationRequest locationRequest = new LocationRequest()
            .setInterval(LOCATION_UPDATE_INTERVAL_MS)
            .setFastestInterval(LOCATION_UPDATE_FASTEST_INTERVAL_MS)
            .setPriority(LocationRequest.PRIORITY_LOW_POWER);

    /**
     * Whether tha location receiver is registered
     */
    protected static boolean isRegisteredLocationReceiver = false;

    protected static Location lastLocation;

    /**
     * Broadcast receiver for location intent
     */
    private final LocationListener locationChangedReceiver = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "Location changed");
            Log.d(TAG, "Provider: " + location.getProvider());
            Log.d(TAG, "Lat: " + location.getLatitude());
            Log.d(TAG, "Long: " + location.getLongitude());
            Log.d(TAG, "Altitude: " + location.getAltitude());
            Log.d(TAG, "Accuracy: " + location.getAccuracy());
            updateLocation(location);
        }
    };

    public LocationColumn(Context context, Typeface paintTypeface, Float paintTextSize, int paintColor) {
        super(context, paintTypeface, paintTextSize, paintColor);

        // Add the helper
        permissionsHelper = new PermissionsHelper(context, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BODY_SENSORS});

    }

    public void updateLocation(Location location) {
        lastLocation = location;
    }

    @Override
    public void setIsVisible(Boolean isVisible) {
        super.setIsVisible(isVisible);
        if (isVisible) {
            if (googleApiClient != null && !googleApiClient.isConnected()) {
                googleApiClient.connect();
            }
        } else {
            if (googleApiClient != null && googleApiClient.isConnected()) {
                unregisterLocationReceiver();
                googleApiClient.disconnect();
            }
        }

    }

    private void registerLocationReceiver() {
        if (!googleApiClient.isConnected()) {
            Log.d(TAG, "Google API client is not ready yet, wont register for location updates");
            return;
        }
        if (isRegisteredLocationReceiver) {
            Log.d(TAG, "Location listener is registered nothing to do");
            return;
        }
        // Check permissions (hopefully the receiver wont be registered
        if (!permissionsHelper.hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            if (permissionsHelper.canAskAgainForPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
                permissionsHelper.askForPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
                Log.d(TAG, "Asking for location permissions");
            }
            return;
        }

        isRegisteredLocationReceiver = true;
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationChangedReceiver);
        Log.d(TAG, "Listening for location updates");
    }

    private void unregisterLocationReceiver() {
        if (!googleApiClient.isConnected()) {
            Log.d(TAG, "Google API client is not ready yet, wont unregister listener");
            return;
        }
        if (!isRegisteredLocationReceiver) {
            Log.d(TAG, "Location listener is not registered nothing to do");
            return;
        }
        isRegisteredLocationReceiver = false;
        LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationChangedReceiver);
        Log.d(TAG, "Stopped listening for location updates");
    }

    @Override
    public void start() {
        super.start();
        if (googleApiClient == null) {
            // Get a Google API client
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        if (!googleApiClient.isConnected()) {
            googleApiClient.connect();
        }

        // Maybe move to start
        if (EmulatorHelper.isEmulator()) {
            Location location = new Location("dummy");
            location.setLatitude(41);
            location.setLongitude(11);
            location.setTime(System.currentTimeMillis());
            location.setAccuracy(3.0f);
            updateLocation(location);
            // Force on task in column
            //updateSunriseAndSunset(location);
//                deactivateAllSensors();
//                watchFace.addSensorColumn(Sensor.TYPE_HEART_RATE);
//                watchFace.updateSensorText(Sensor.TYPE_HEART_RATE, "128");
        }
    }

    @Override
    public void destroy() {
        if (googleApiClient != null && googleApiClient.isConnected()) {
            unregisterLocationReceiver();
            googleApiClient.disconnect();
        }
        super.destroy();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Google Api Connected");
        registerLocationReceiver();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Google Api connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.w(TAG, "Google Api connetion failed");
    }
}