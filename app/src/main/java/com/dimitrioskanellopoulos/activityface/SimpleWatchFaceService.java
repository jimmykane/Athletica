package com.dimitrioskanellopoulos.activityface;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceHolder;

import android.location.Location;

import java.lang.ref.WeakReference;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class SimpleWatchFaceService extends CanvasWatchFaceService {

    /**
     * Update rate in milliseconds for interactive mode. We update once a second since seconds are
     * displayed in interactive mode.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    /**
     * Handler message id for updating the time periodically in interactive mode.
     */
    private static final int MSG_UPDATE_TIME = 0;

    @Override
    public CanvasWatchFaceService.Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends CanvasWatchFaceService.Engine implements
             PressureSensor.PressureChangeCallback {

        private static final String TAG = "Engine";

        final Handler mUpdateTimeHandler = new EngineHandler(this);

        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                watchFace.updateTimeZoneWith(intent.getStringExtra("time-zone"));
            }
        };

        final BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
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

        boolean mRegisteredTimeZoneReceiver = false;

        private SimpleWatchFace watchFace;

        private LocationEngine locationEngine;

        private PressureSensor pressureSensor;

        private GoogleApiHelper googleApiHelper;

        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        boolean mLowBitAmbient;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            setWatchFaceStyle(new WatchFaceStyle.Builder(SimpleWatchFaceService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setAmbientPeekMode(WatchFaceStyle.AMBIENT_PEEK_MODE_HIDDEN)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .build());

            watchFace = new SimpleWatchFace(SimpleWatchFaceService.this);

            googleApiHelper = new GoogleApiHelper(SimpleWatchFaceService.this);

            locationEngine = new LocationEngine(googleApiHelper);

            pressureSensor = new PressureSensor(getApplicationContext(), this);

            registerBatteryInfoReceiver();
            updateSunriseAndSunset();
            pressureSensor.startListening();
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            unregisterBatteryInfoReceiver();
            googleApiHelper.disconnect();
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                registerTimeZoneReceiver();
                // Update time zone in case it changed while we weren't visible.
                watchFace.updateTimeZoneWith(TimeZone.getDefault().getID());
            } else {
                unregisterTimeZoneReceiver();
            }
            updateSunriseAndSunset();
            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        private void registerTimeZoneReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            SimpleWatchFaceService.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterTimeZoneReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            SimpleWatchFaceService.this.unregisterReceiver(mTimeZoneReceiver);
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
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
            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        /**
         * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        private void handleUpdateTimeMessage() {
            invalidate();
            if (shouldTimerBeRunning()) {
                long timeMs = System.currentTimeMillis();
                long delayMs = INTERACTIVE_UPDATE_RATE_MS
                        - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
            }
        }

        public void handlePressureValueChanged(Float pressureValue) {
            Float altitude = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressureValue);
            watchFace.updatePressureAltitude(String.format("%.02f", altitude));
            pressureSensor.stopListening();
            Log.d(TAG, "Updated pressure");
        }

        private void updateSunriseAndSunset() {
            Location location = locationEngine.getLastKnownLocation();
            if (location == null){
                // If its a real device continue to run
                // @todo solve this with timezone
                if (!isEmulator()) {
                    Log.e(TAG, "Could not update sunrise/sunset because no location was found");
                    return;
                }
                location = new Location("dummyprovider");
                location.setLatitude(20.3);
                location.setLongitude(52.6);
            }
            Pair<String, String> sunriseSunset = SunriseSunsetTimesService.getSunriseAndSunset(location, TimeZone.getDefault().getID());
            watchFace.updateSunriseSunset(sunriseSunset);
            Log.d(TAG, "Successfully updated sunrise");
        }

        private void unregisterBatteryInfoReceiver() {
            unregisterReceiver(batteryInfoReceiver);
        }

        private void registerBatteryInfoReceiver() {
            registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        }

        private void checkActions(){
            long timeMillis=System.currentTimeMillis();
            long totalSeconds=timeMillis/1000;
            int second=(int)(totalSeconds%60);
            long totalMinutes=totalSeconds/60;
            int minute=(int)(totalMinutes%60);
            long totalHours=totalMinutes/60;
            int hour=(int)(totalHours%24);
            if ((minute%5) == 0) {
                pressureSensor.startListening();
            }
            if ((minute%5) == 0){
                updateSunriseAndSunset();
            }
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



    private static class EngineHandler extends Handler {
        private final WeakReference<SimpleWatchFaceService.Engine> mWeakReference;

        public EngineHandler(SimpleWatchFaceService.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            SimpleWatchFaceService.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }

    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }
}
