package com.dimitrioskanellopoulos.activityface;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceHolder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;

import android.location.Location;

import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class SimpleWatchFaceService extends CanvasWatchFaceService {

    private static final long TICK_PERIOD_MILLIS = TimeUnit.SECONDS.toMillis(1);

    @Override
    public Engine onCreateEngine() {
        return new SimpleEngine();
    }

    private class SimpleEngine extends CanvasWatchFaceService.Engine implements
            GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, PressureSensor.PressureChangeCallback {

        private static final String TAG = "SimpleEngine";

        private SimpleWatchFace watchFace;

        private Handler timeTick;

        private GoogleApiClient googleApiClient;

        private Location lastKnownLocation;

        private PressureSensor pressureSensor;

        /**
         * When pressure changes
         * @param pressureValue
         */
        public void pressureValueChanged(Float pressureValue) {
            // Get the alti from pressure
            Float altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressureValue);
            watchFace.updatePressureAltitude(String.format("%.02f", altitude));
            Log.e(TAG, "Updated pressure");
            // Stop the listening
            pressureSensor.stopListening();
            // Invalidate to redraw
            invalidate();
        }

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(SimpleWatchFaceService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setAmbientPeekMode(WatchFaceStyle.AMBIENT_PEEK_MODE_HIDDEN)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());

            timeTick = new Handler(Looper.myLooper());

            startTimerIfNecessary();

            watchFace = SimpleWatchFace.newInstance(SimpleWatchFaceService.this);

            googleApiClient = new GoogleApiClient.Builder(SimpleWatchFaceService.this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();

            pressureSensor = new PressureSensor(getApplicationContext(), this);

            registerBatteryInfoReceiver();
            updateSunriseAndSunset();
        }

        private void startTimerIfNecessary() {
            timeTick.removeCallbacks(timeRunnable);
            if (isVisible() && !isInAmbientMode()) {
                timeTick.post(timeRunnable);
            }
        }

        private final Runnable timeRunnable = new Runnable() {
            @Override
            public void run() {
                onSecondTick();
                if (isVisible() && !isInAmbientMode()) {
                    timeTick.postDelayed(this, TICK_PERIOD_MILLIS);
                }
            }
        };

        private void onSecondTick() {
            invalidateIfNecessary();
        }

        private void invalidateIfNecessary() {
            if (isVisible() && !isInAmbientMode()) {
                invalidate();
            }
        }

        private void updateAltitude(){
            pressureSensor.startListening();
        }

        private void updateSunriseAndSunset() {
            // Try once more to get the loc
            if (googleApiClient != null && googleApiClient.isConnected()){
                lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            }
            Pair<String, String> sunriseSunset = SunriseSunsetTimesService.getSunriseAndSunset(lastKnownLocation, TimeZone.getDefault().getID());
            watchFace.updateSunrise(sunriseSunset.first);
            watchFace.updateSunset(sunriseSunset.second);
            Log.e(TAG, "Updated sunrise");
        }

        private BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = context.registerReceiver(null, ifilter);
                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                float batteryPct = level / (float)scale; // Used just in case
                watchFace.updateBatteryLevel(Integer.toString(level));
            }
        };

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                googleApiClient.connect();
            } else {
                releaseGoogleApiClient();
            }
            startTimerIfNecessary();
        }

        private void releaseGoogleApiClient() {
            if (googleApiClient != null && googleApiClient.isConnected()) {
                googleApiClient.disconnect();
            }
        }

        private void unregisterBatteryInfoReceiver() {
            unregisterReceiver(batteryInfoReceiver);
        }

        private void registerBatteryInfoReceiver() {
            registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            super.onDraw(canvas, bounds);
            watchFace.draw(canvas, bounds);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            checkActions();
            invalidate();
        }

        private void checkActions(){
            long totalMilliSeconds=System.currentTimeMillis();
            long totalSeconds=totalMilliSeconds/1000;
            int second=(int)(totalSeconds%60);
            long totalMinutes=totalSeconds/60;
            int minute=(int)(totalMinutes%60);
            long totalHours=totalMinutes/60;
            int hour=(int)(totalHours%24);
            if ((minute%5) == 0) {
                updateAltitude();
            }
            if ((hour%2) == 0 && minute == 0){
                updateSunriseAndSunset();
            }
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            watchFace.setAntiAlias(!inAmbientMode);
            watchFace.setShowSeconds(!isInAmbientMode());

            if (inAmbientMode) {
                watchFace.updateBackgroundColourToDefault();
                watchFace.updateDateAndTimeColourToDefault();
            } else {
                updateAltitude();
                watchFace.restoreBackgroundColour();
                watchFace.restoreDateAndTimeColour();
            }
            invalidate();
            startTimerIfNecessary();
        }

        @Override
        public void onDestroy() {
            timeTick.removeCallbacks(timeRunnable);
            releaseGoogleApiClient();
            unregisterBatteryInfoReceiver();
            super.onDestroy();
        }

        @Override
        public void onConnected(Bundle connectionHint) {
            // Provides a simple way of getting a device's location and is well suited for
            // applications that do not require a fine-grained location and that do not need location
            // updates. Gets the best and most recent location currently available, which may be null
            // in rare cases when a location is not available.
            lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if (lastKnownLocation != null) {
                Log.e(TAG, "Location found");
                updateSunriseAndSunset();
                return;
            }

            // Create the LocationRequest object
            // LocationRequest locationRequest = LocationRequest.create();
            // Use high accuracy
            // locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
            // Set the update interval to 2 seconds
            // locationRequest.setInterval(TimeUnit.SECONDS.toMillis(2));
            // Set the fastest update interval to 2 seconds
            // locationRequest.setFastestInterval(TimeUnit.SECONDS.toMillis(2));
            // Set the minimum displacement
            // locationRequest.setSmallestDisplacement(2);
            // Register listener using the LocationRequest object
            // LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
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
}
