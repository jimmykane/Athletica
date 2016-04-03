package com.dimitrioskanellopoulos.activityface;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.view.SurfaceHolder;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.Wearable;
import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;

import android.location.Location;
import android.location.LocationManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class SimpleWatchFaceService extends CanvasWatchFaceService {

    private static final long TICK_PERIOD_MILLIS = TimeUnit.SECONDS.toMillis(1);

    @Override
    public Engine onCreateEngine() {
        return new SimpleEngine();
    }

    private class SimpleEngine extends CanvasWatchFaceService.Engine implements
            GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


        private static final String TAG = "SimpleEngine";

        private SimpleWatchFace watchFace;
        private Handler timeTick;

        private GoogleApiClient googleApiClient;

        private Location lastKnownLocation;

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

        private void getSunriseAndSunset(com.luckycatlabs.sunrisesunset.dto.Location location) {
            SunriseSunsetCalculator calculator = new SunriseSunsetCalculator(location, "Europe/Paris");
            String officialSunrise = calculator.getOfficialSunriseForDate(Calendar.getInstance());
            String officialSunset = calculator.getOfficialSunsetForDate(Calendar.getInstance());
            watchFace.updateSunrise(officialSunrise);
            watchFace.updateSunset(officialSunset);
        }

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

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            super.onDraw(canvas, bounds);
            watchFace.draw(canvas, bounds);
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
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
                watchFace.restoreBackgroundColour();
                watchFace.restoreDateAndTimeColour();
            }

            invalidate();

            startTimerIfNecessary();

            String latitude = "42.919532";
            String longitude = "1.035006";

            if (lastKnownLocation != null) {
                latitude = String.valueOf(lastKnownLocation.getLatitude());
                longitude = String.valueOf(lastKnownLocation.getLongitude());
            }

            com.luckycatlabs.sunrisesunset.dto.Location location = new com.luckycatlabs.sunrisesunset.dto.Location(Double.parseDouble(latitude), Double.parseDouble(longitude));
            getSunriseAndSunset(location);
        }

        @Override
        public void onDestroy() {
            timeTick.removeCallbacks(timeRunnable);
            super.onDestroy();
        }

        @Override
        public void onConnected(Bundle connectionHint) {
            lastKnownLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.e(TAG, "suspended GoogleAPI");
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Log.e(TAG, "connectionFailed GoogleAPI");
        }
    }
}
