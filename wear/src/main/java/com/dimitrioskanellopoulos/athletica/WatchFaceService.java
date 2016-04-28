package com.dimitrioskanellopoulos.athletica;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceHolder;

import android.location.Location;
import android.view.WindowInsets;
import android.widget.Toast;

import com.dimitrioskanellopoulos.athletica.sensors.AveragingCallbackSensor;
import com.dimitrioskanellopoulos.athletica.sensors.CallbackSensorFactory;
import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorAverageEventCallbackInterface;
import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorEventCallbackInterface;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class WatchFaceService extends CanvasWatchFaceService {

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
            OnSensorEventCallbackInterface, OnSensorAverageEventCallbackInterface,
            GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

        private static final String TAG = "Engine";

        private static final String PERMISSIONS_GRANTED_MESSAGE = "PERMISSIONS_GRANTED_MESSAGE";

        /**
         * Handler for updating the time
         */
        private final Handler mUpdateTimeHandler = new EngineHandler(this);

        /**
         * Broadcast receiver for updating the timezone
         */
        private final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                watchFace.updateTimeZoneWith(TimeZone.getTimeZone(intent.getStringExtra("time-zone")));
            }
        };

        /**
         * Broadcast receiver for updating the battery level
         */
        private final BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = context.registerReceiver(null, ifilter);
                // Just in case
                int level = 0;
                if (batteryStatus != null) {
                    level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                }
                //int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                //float batteryPct = level / (float) scale;
                watchFace.updateBatteryLevel(level);
            }
        };

        /**
         * Broadcast receiver for when a the permissions request has granted permissions
         */
        private BroadcastReceiver permissionsGrantedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                findAndSetAvailableSensorTypes();
                Toast.makeText(getApplicationContext(), "Enabled permission: " + intent.getExtras().get("permission"), Toast.LENGTH_SHORT).show();
            }
        };

        /**
         * Broadcast receiver for location intent
         */
        private LocationListener locationChangedReceiver = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d(TAG, "Location changed");
                Log.d(TAG, "Provider: " + location.getProvider());
                Log.d(TAG, "Lat: " + location.getLatitude());
                Log.d(TAG, "Long: " + location.getLongitude());
                Log.d(TAG, "Altitude: " + location.getAltitude());
                Log.d(TAG, "Accuracy: " + location.getAccuracy());
                vibrator.vibrate(new long[]{0, 50}, -1);
                updateSunriseAndSunset(location);
            }
        };

        /**
         * Whether tha timezone receiver is registered
         */
        boolean isRegisteredTimeZoneReceiver = false;

        /**
         * Whether tha location receiver is registered
         */
        boolean isRegisteredLocationReceiver = false;

        /**
         * The watchface. Used for drawing and updating the view/watchface
         */
        private WatchFace watchFace;

        /**
         * A helper for google api that can be shared within the app
         */
        private GoogleApiClient googleApiClient;

        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        private boolean mLowBitAmbient;

        /**
         * The supported sensor types for this watch face. Not the supported ones by the device
         */
        private int[] supportedSensorTypes = {
                Sensor.TYPE_PRESSURE,
                Sensor.TYPE_HEART_RATE,
                Sensor.TYPE_TEMPERATURE,
                Sensor.TYPE_AMBIENT_TEMPERATURE,
                Sensor.TYPE_LIGHT,
                Sensor.TYPE_MAGNETIC_FIELD,
                Sensor.TYPE_ACCELEROMETER,
        };

        /**
         * The available sensors. Cross of supported by the app sensors and supported by the device
         */
        private ArrayList<Integer> availableSensorTypes = new ArrayList<Integer>();

        /**
         * How many sensors we want to utilize concurently
         */
        final Integer maxActiveSensors = 1;

        /**
         * The active sensors list. These sensors are the active ones at runtime
         */
        private final LinkedHashMap<Integer, AveragingCallbackSensor> activeSensors = new LinkedHashMap<Integer, AveragingCallbackSensor>();

        /**
         * Don't be kinky on this. It's the virbrating system service. Useful for haptic feedback
         */
        private final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        /**
         * The sensor manager service
         */
        private final SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        /**
         * The location request we will be making
         */
        private final LocationRequest locationRequest = new LocationRequest().setInterval(10).setFastestInterval(10).setPriority(LocationRequest.PRIORITY_LOW_POWER);

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            // Set the style
            setWatchFaceStyle(new WatchFaceStyle.Builder(WatchFaceService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setAmbientPeekMode(WatchFaceStyle.AMBIENT_PEEK_MODE_HIDDEN)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setAcceptsTapEvents(true)
                    .setShowSystemUiTime(false)
                    .build());

            // Create a watch face
            watchFace = new WatchFace(WatchFaceService.this);

            // Get a Google API client
            googleApiClient = new GoogleApiClient.Builder(WatchFaceService.this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();

            // Connect to Google API
            googleApiClient.connect();

            // Finds and sets the available sensor types
            findAndSetAvailableSensorTypes();

            // Activate the "next" sensors
            activateNextSensors();

            // Listen to any permissions granted broadcast in order to enable functionality
            registerPermissionsGrantedReceiver();
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            stopListeningToSensors();
            unregisterPermissionsGrantedReceiver();
            if (googleApiClient.isConnected()) {
                googleApiClient.disconnect();
            }
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            Log.d(TAG, "Visibility changed: " + visible);
            if (visible) {
                // Check for timezone changes
                registerTimeZoneReceiver();
                // Check for battery changes
                registerBatteryInfoReceiver();
                // Start updating sensor values
                startListeningToSensors();
                // Update time zone in case it changed while we weren't visible.
                watchFace.updateTimeZoneWith(TimeZone.getDefault());
            } else {
                // Stop checking for timezone updates
                unregisterTimeZoneReceiver();
                // Stop checking for battery level changes
                unregisterBatteryInfoReceiver();
                // Stop updating sensor values
                stopListeningToSensors();
            }
            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            // Obvious
            watchFace.setAntiAlias(!inAmbientMode);
            watchFace.setShowSeconds(!isInAmbientMode());

            // When we are active show realtime data from the sensors. Start listening
            if (inAmbientMode) {
                stopListeningToSensors();
            } else {
                startListeningToSensors();
            }
            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
        }

        @Override
        public void onApplyWindowInsets(WindowInsets insets) {
            super.onApplyWindowInsets(insets);
            watchFace.setIsRound(insets.isRound());
            watchFace.setChinSize(insets.getSystemWindowInsetBottom());
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            runOnTimeTickTasks();
            invalidate();
        }

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {
            super.onDraw(canvas, bounds);
            watchFace.draw(canvas, bounds);
        }

        @Override
        public void onTapCommand(
                @TapType int tapType, int x, int y, long eventTime) {
            switch (tapType) {
                case WatchFaceService.TAP_TYPE_TAP:
                    activateNextSensors();
                    startListeningToSensors();
                    vibrator.vibrate(new long[]{0, 50, 50, 50, 50}, -1);
                    break;

                case WatchFaceService.TAP_TYPE_TOUCH:
                    break;

                case WatchFaceService.TAP_TYPE_TOUCH_CANCEL:
                    break;
            }
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.d(TAG, "Google API connected");
            registerLocationReceiver();
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d(TAG, "Google API connection suspended");
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.d(TAG, "Google API connection failed");
        }

        @Override
        public void handleOnSensorChangedEvent(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_PRESSURE:
                    event.values[0] = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, event.values[0]);
                default:
                    break;
            }
            watchFace.updateSensorPaintText(event.sensor.getType(), String.format("%d", Math.round(event.values[0])));
            Log.d(TAG, "Updated value for sensor: " + event.sensor.getStringType());
            Log.d(TAG, "Invalidating view");
            postInvalidate();
        }

        @Override
        public void handleOnSensorAverageChangedEvent(SensorEvent event) {
            handleOnSensorChangedEvent(event);
        }

        /**
         * Finds and sets all the available and supported sensors
         */
        private void findAndSetAvailableSensorTypes() {
            // Clear all enabled
            availableSensorTypes.clear();
            // Add the ones supported by the device and the app
            for (int supportedSensorType : supportedSensorTypes) {
                if (sensorManager.getDefaultSensor(supportedSensorType) != null) {
                    Log.d(TAG, "Available sensor: " + sensorManager.getDefaultSensor(supportedSensorType).getStringType());
                    availableSensorTypes.add(supportedSensorType);
                }
            }
        }

        private void registerTimeZoneReceiver() {
            if (isRegisteredTimeZoneReceiver) {
                return;
            }
            isRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            WatchFaceService.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterTimeZoneReceiver() {
            if (!isRegisteredTimeZoneReceiver) {
                return;
            }
            isRegisteredTimeZoneReceiver = false;
            WatchFaceService.this.unregisterReceiver(mTimeZoneReceiver);
        }

        private void registerLocationReceiver(){
            if (isRegisteredLocationReceiver){
                return;
            }
            isRegisteredLocationReceiver = true;
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationChangedReceiver);
        }

        private void unregisterLocationReceiver(){
            if (!isRegisteredLocationReceiver){
                return;
            }
            isRegisteredLocationReceiver = false;
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, locationChangedReceiver);
        }

        private void registerPermissionsGrantedReceiver() {
            registerReceiver(permissionsGrantedReceiver, new IntentFilter(PERMISSIONS_GRANTED_MESSAGE));
        }

        private void unregisterPermissionsGrantedReceiver() {
            unregisterReceiver(permissionsGrantedReceiver);
        }

        private void unregisterBatteryInfoReceiver() {
            unregisterReceiver(batteryInfoReceiver);
        }

        private void registerBatteryInfoReceiver() {
            registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
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

        /**
         * Activates the next sensors
         */
        private void activateNextSensors() {
            // Find the active sensors position in the available sensors
            int countFound = 0;
            int lastFoundIndex = 0;
            for (Integer availableSensorType : availableSensorTypes) {
                if (!activeSensors.containsKey(availableSensorType)) {
                    continue;
                }
                // Found one
                countFound += 1;
                // If we found all
                if (countFound == maxActiveSensors) {
                    // Get the index that the last was found
                    lastFoundIndex = availableSensorTypes.indexOf(availableSensorType);
                    // Stop we don't need to loop more
                    break;
                }
            }
            // Deactivate all sensors
            deactivateAllSensors();
            // Enable the next ones (+1)
            for (int i = 0; i < maxActiveSensors; i++) {
                // Check if we hit the last
                lastFoundIndex += 1;
                if (lastFoundIndex == availableSensorTypes.size()) {
                    // Reset the index to start
                    lastFoundIndex = 0;
                }
                // Activate
                activateSensor(availableSensorTypes.get(lastFoundIndex));
            }
        }

        /**
         * Activate a specific type of sensor
         */
        private void activateSensor(Integer sensorType) {
            activeSensors.put(sensorType, CallbackSensorFactory.getCallbackSensor(getApplicationContext(), sensorType, this, this));
            watchFace.addSensorPaint(sensorType);
        }

        /**
         * Deactivate a specific type of sensor
         */
        private void deactivateSensor(Integer sensorType) {
            activeSensors.get(sensorType).stopListening();
            activeSensors.remove(sensorType);
            watchFace.removeSensorPaint(sensorType);
        }

        /**
         * Deactivate all types of active sensors
         */
        private void deactivateAllSensors() {
            for (Map.Entry<Integer, AveragingCallbackSensor> entry : activeSensors.entrySet()) {
                deactivateSensor(entry.getKey());
            }
        }

        /**
         * Updates the sunrise and sunset according to a location if possible
         */
        private void updateSunriseAndSunset(Location location) {
            if (location == null) {
                // If its a real device continue to run
                Log.e(TAG, "Could not update sunrise/sunset because no location was found");
                return;
            }
            Pair<String, String> sunriseSunset = SunriseSunsetTimesService.getSunriseAndSunset(location, TimeZone.getDefault().getID());
            watchFace.updateSunriseSunset(sunriseSunset);
            invalidate();
            Log.d(TAG, "Successfully updated sunrise");
        }

        private void startListeningToSensors() {
            for (Map.Entry<Integer, AveragingCallbackSensor> entry : activeSensors.entrySet()) {
                entry.getValue().startListening();
            }
        }

        private void calculateAverageForActiveSensors() {
            for (Map.Entry<Integer, AveragingCallbackSensor> entry : activeSensors.entrySet()) {
                entry.getValue().getAverage();
            }
        }

        private void stopListeningToSensors() {
            for (Map.Entry<Integer, AveragingCallbackSensor> entry : activeSensors.entrySet()) {
                entry.getValue().stopListening();
            }
        }

        /**
         * Run's tasks according to the current time
         */
        private void runOnTimeTickTasks() {
            // @todo this is wrong
            Calendar rightNow = Calendar.getInstance();
            int hour = rightNow.get(Calendar.HOUR_OF_DAY);
            int minute = rightNow.get(Calendar.MINUTE);
            int second = rightNow.get(Calendar.SECOND);
            // Every 15 minutes
            if (minute % 15 == 0) {
                calculateAverageForActiveSensors();
            }
        }
    }

    /**
     * Handler for various messages
     */
    private static class EngineHandler extends Handler {
        private final WeakReference<WatchFaceService.Engine> mWeakReference;

        public EngineHandler(WatchFaceService.Engine reference) {
            mWeakReference = new WeakReference<>(reference);
        }

        @Override
        public void handleMessage(Message msg) {
            WatchFaceService.Engine engine = mWeakReference.get();
            if (engine != null) {
                switch (msg.what) {
                    case MSG_UPDATE_TIME:
                        engine.handleUpdateTimeMessage();
                        break;
                }
            }
        }
    }
}
