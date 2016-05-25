package com.dimitrioskanellopoulos.athletica;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Location;
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
import android.view.WindowInsets;

import com.dimitrioskanellopoulos.athletica.configuration.ConfigurationHelper;
import com.dimitrioskanellopoulos.athletica.helpers.EmulatorHelper;
import com.dimitrioskanellopoulos.athletica.helpers.SensorHelper;
import com.dimitrioskanellopoulos.athletica.helpers.SunriseSunsetHelper;
import com.dimitrioskanellopoulos.athletica.permissions.PermissionsHelper;
import com.dimitrioskanellopoulos.athletica.sensors.CallbackSensor;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
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

    /**
     * The location update intervals: 1hour in ms
     */
    private static final long LOCATION_UPDATE_INTERVAL_MS = 3600000;
    private static final long LOCATION_UPDATE_FASTEST_INTERVAL_MS = 3600000;
    /**
     * How often the onTimeTick actions should run
     * 15 minutes for real device and 1 minute for emulator
     */
    private static final long RUN_ON_TICK_TASKS_EVERY_MS = !EmulatorHelper.isEmulator() ? 15 * 60 * 1000 : 1 * 60 * 1000;

    @Override
    public CanvasWatchFaceService.Engine onCreateEngine() {
        return new Engine();
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

    private class Engine extends CanvasWatchFaceService.Engine implements
            GoogleApiClient.ConnectionCallbacks,
            DataApi.DataListener,
            GoogleApiClient.OnConnectionFailedListener {
        private static final String TAG = "Engine";

        /**
         * Handler for updating the time
         */
        private final Handler mUpdateTimeHandler = new EngineHandler(this);
        /**
         * Don't be kinky on this. It's the vibrating system service. Useful for haptic feedback
         */
        private final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        /**
         * The sensor manager service
         */
        private final SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        /**
         * The location request we will be making
         */
        private final LocationRequest locationRequest = new LocationRequest()
                .setInterval(LOCATION_UPDATE_INTERVAL_MS)
                .setFastestInterval(LOCATION_UPDATE_FASTEST_INTERVAL_MS)
                .setPriority(LocationRequest.PRIORITY_LOW_POWER);
        /**
         * Whether tha Battery receiver is registered
         */
        boolean isRegisteredBatteryInfoReceiver = false;

        /**
         * Whether tha location receiver is registered
         */
        boolean isRegisteredLocationReceiver = false;
        /**
         * When the onTickActions were run last time in ms
         */
        private Calendar lastOnTimeTickTasksRun = Calendar.getInstance();
        /**
         * Whether tha timezone receiver is registered
         */
        private boolean isRegisteredTimeZoneReceiver = false;
        /**
         * The watchface. Used for drawing and updating the view/watchface
         */
        private WatchFace watchFace;
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
                updateSunriseAndSunset(location);
            }
        };
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
         * The normal watch face style for white on black background
         */
        private final WatchFaceStyle watchFaceStyleNormal = new WatchFaceStyle.Builder(WatchFaceService.this)
                .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                .setAmbientPeekMode(WatchFaceStyle.AMBIENT_PEEK_MODE_HIDDEN)
                .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                .setAcceptsTapEvents(true)
                .setShowSystemUiTime(false)
                .build();

        /**
         * The inverted watch face style for black on white background
         */
        private final WatchFaceStyle watchFaceStyleInverted = new WatchFaceStyle.Builder(WatchFaceService.this)
                .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                .setAmbientPeekMode(WatchFaceStyle.AMBIENT_PEEK_MODE_HIDDEN)
                .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                .setAcceptsTapEvents(true)
                .setShowSystemUiTime(false)
                .setViewProtectionMode(WatchFaceStyle.PROTECT_STATUS_BAR | WatchFaceStyle.PROTECT_HOTWORD_INDICATOR)
                .build();
        /**
         * The available sensors. Cross of supported by the app sensors and supported by the device
         */
        private ArrayList<Integer> availableSensorTypes = new ArrayList<>();
        private PermissionsHelper permissionsHelper;

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            // Set the style start with normal
            setWatchFaceStyle(watchFaceStyleNormal);

            // Create a watch face
            watchFace = new WatchFace(WatchFaceService.this);
            watchFace.setInAmbientMode(false);

            // Add the helper
            permissionsHelper = new PermissionsHelper(getApplicationContext(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BODY_SENSORS});

            // Get a Google API client
            googleApiClient = new GoogleApiClient.Builder(WatchFaceService.this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Wearable.API)
                    .addApi(LocationServices.API)
                    .build();
        }

        @Override
        public void onDestroy() {
            Log.d(TAG, "onDestroy");
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            unregisterBatteryInfoReceiver();
            unregisterTimeZoneReceiver();
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
                // Connect to Google API
                googleApiClient.connect();
                // Check for timezone changes
                registerTimeZoneReceiver();
                // Check for battery changes
                registerBatteryInfoReceiver();
                // Update time zone in case it changed while we weren't visible.
                watchFace.updateTimeZoneWith(TimeZone.getDefault());
            } else {
                // Stop checking for timezone updates
                unregisterTimeZoneReceiver();
                // Stop checking for battery level changes
                unregisterBatteryInfoReceiver();

                if (googleApiClient != null && googleApiClient.isConnected()) {
                    Wearable.DataApi.removeListener(googleApiClient, this);
                    // Unregister location receiver to save up in case of a foreground app
                    unregisterLocationReceiver();
                    googleApiClient.disconnect();
                }
            }
            watchFace.setIsVisible(visible);
            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            // Obvious
            watchFace.setInAmbientMode(inAmbientMode);

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
                    // If there is another sensor
                    if (availableSensorTypes.size() < 2) {
                        break;
                    }
                    addSensorColumn();
                    vibrator.vibrate(new long[]{0, 50, 50}, -1);
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
            Wearable.DataApi.addListener(googleApiClient, Engine.this);
            updateConfigDataItemAndUiOnStartup();
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.d(TAG, "Google API connection suspended");
        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.d(TAG, "Google API connection failed");
        }

        @Override // DataApi.DataListener
        public void onDataChanged(DataEventBuffer dataEvents) {
            Log.d(TAG, "Data changed");
            for (DataEvent dataEvent : dataEvents) {
                if (dataEvent.getType() != DataEvent.TYPE_CHANGED) {
                    continue;
                }

                DataItem dataItem = dataEvent.getDataItem();
                if (!dataItem.getUri().getPath().equals(
                        ConfigurationHelper.PATH_WITH_FEATURE)) {
                    continue;
                }

                DataMapItem dataMapItem = DataMapItem.fromDataItem(dataItem);
                DataMap config = dataMapItem.getDataMap();
                Log.d(TAG, "Config DataItem updated:" + config);
                // This can happen from this method more often when phone changes
                updateUiForConfigDataMap(config);
            }
        }

        private void updateConfigDataItemAndUiOnStartup() {
            ConfigurationHelper.fetchConfigDataMap(googleApiClient,
                    new ConfigurationHelper.FetchConfigDataMapCallback() {
                        @Override
                        public void onConfigDataMapFetched(DataMap startupConfig) {
                            // If the DataItem hasn't been created yet or some keys are missing,
                            // use the default values.
                            ConfigurationHelper.setDefaultValuesForMissingConfigKeys(getApplicationContext(), startupConfig);
                            ConfigurationHelper.putConfigDataItem(googleApiClient, startupConfig);
                            updateUiForConfigDataMap(startupConfig);
                        }
                    }
            );
        }

        private void updateUiForConfigDataMap(final DataMap config) {
            boolean uiUpdated = false;
            for (String key : config.keySet()) {
                if (!config.containsKey(key)) {
                    Log.w(TAG, "No value found for config key:" + key);
                    continue;
                }
                switch (key) {
                    case ConfigurationHelper.KEY_TIME_FORMAT:
                        watchFace.setTimeFormat24(config.getBoolean(key));
                        break;
                    case ConfigurationHelper.KEY_DATE_NAMES:
                        watchFace.setShowDateNamesFormat(config.getBoolean(key));
                        break;
                    case ConfigurationHelper.KEY_INTERLACE:
                        watchFace.shouldInterlace(config.getBoolean(key));
                        break;
                    case ConfigurationHelper.KEY_INVERT_BLACK_AND_WHITE:
                        watchFace.setInvertBlackAndWhite(config.getBoolean(key));
                        setWatchFaceStyle(config.getBoolean(key) ? watchFaceStyleInverted : watchFaceStyleNormal);
                        break;
                    case ConfigurationHelper.KEY_ENABLED_SENSORS:
                        // Save new config
                        setAvailableSensorTypes(config.getIntegerArrayList(key));

                        if (availableSensorTypes.size() == 0) {
                            watchFace.removeAllSensorColumns();
                            break;
                        }

                        Boolean found = false;
                        for (Integer availableSensorType : SensorHelper.getApplicationDeviceSupportedSensors(getApplicationContext())){
                            if (watchFace.hasSensorColumn(availableSensorType)){
                                found = true;
                            }
                        }
                        if (!found){
                            addSensorColumn();
                        }


//                        if (availableSensorTypes.size() > 1) {
//                            DataMap configMap = new DataMap();
//                            configMap.putIntegerArrayList(ConfigurationHelper.KEY_ENABLED_SENSORS,
//                                    new ArrayList<>(availableSensorTypes.subList(0, 1)));
//                            ConfigurationHelper.overwriteKeysInConfigDataMap(googleApiClient, configMap);
//                            Toast.makeText(getApplicationContext(), getResources().getText(R.string.get_pro), Toast.LENGTH_SHORT).show();
//                            break;
//                        }


                        break;
                    default:
                        Log.w(TAG, "Ignoring unknown config key: " + key);
                        break;
                }
                uiUpdated = true;
            }
            if (uiUpdated) {
                invalidate();
            }
        }

        private void addSensorColumn() {
            // Sanity
            if (availableSensorTypes.size() == 0) {
                watchFace.removeAllSensorColumns();
                return;
            }
            // If only one
            if (availableSensorTypes.size() == 1) {
                // If it's not there
                if (!watchFace.hasSensorColumn(availableSensorTypes.get(0))){
                    watchFace.removeAllSensorColumns();
                    watchFace.addSensorColumn(availableSensorTypes.get(0));
                }
                return;
            }
            // Check if found and what position and remove
            int indexFound = -1;
            int i=0;
            for (Integer availableSensorType : availableSensorTypes){
                if (watchFace.hasSensorColumn(availableSensorType)){
                    indexFound = i;
                    break;
                }
                i++;
            }
            watchFace.removeAllSensorColumns();
            // If not found add 1st one
            if (indexFound == -1){
                watchFace.addSensorColumn(availableSensorTypes.get(0));
                return;
            }
            // If last got to 1st
            if ((indexFound + 1) >= availableSensorTypes.size()){
                watchFace.addSensorColumn(availableSensorTypes.get(0));
                return;
            }
            watchFace.addSensorColumn(availableSensorTypes.get(indexFound+1));
        }

        /**
         * Finds and sets all the available and supported sensors
         */
        private void setAvailableSensorTypes(ArrayList<Integer> sensorTypes) {
            // Clear all enabled
            availableSensorTypes.clear();
            for (int sensorType : sensorTypes) {
                switch (sensorType) {
                    case Sensor.TYPE_HEART_RATE:
                        if (sensorManager.getDefaultSensor(sensorType) == null) {
                            Log.w(TAG, "Could not add to available sensors sensor: " + sensorType);
                            continue;
                        }
                        Log.d(TAG, "Available sensor: " + sensorManager.getDefaultSensor(sensorType).getStringType());
                        break;
                    case CallbackSensor.TYPE_PRESSURE_ALTITUDE:
                        if (sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE) == null) {
                            Log.w(TAG, "Could not add to available sensors sensor: " + sensorType);
                            continue;
                        }
                        Log.d(TAG, "Available sensor: pressure_altitude");
                        break;
                    default:
                        if (sensorManager.getDefaultSensor(sensorType) == null) {
                            Log.w(TAG, "Could not add to available sensors sensor: " + sensorType);
                            continue;
                        }
                        Log.d(TAG, "Available sensor: " + sensorManager.getDefaultSensor(sensorType).getStringType());
                        break;
                }
                availableSensorTypes.add(sensorType);
            }
        }



        private void registerTimeZoneReceiver() {
            if (isRegisteredTimeZoneReceiver) {
                return;
            }
            isRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterTimeZoneReceiver() {
            if (!isRegisteredTimeZoneReceiver) {
                return;
            }
            isRegisteredTimeZoneReceiver = false;
            unregisterReceiver(mTimeZoneReceiver);
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

        private void unregisterBatteryInfoReceiver() {
            if (!isRegisteredBatteryInfoReceiver) {
                return;
            }
            unregisterReceiver(batteryInfoReceiver);
            isRegisteredBatteryInfoReceiver = false;
        }

        private void registerBatteryInfoReceiver() {
            if (isRegisteredBatteryInfoReceiver) {
                return;
            }
            registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            isRegisteredBatteryInfoReceiver = true;
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
//        private void activateNextAvailableSensors() {
//            Log.d(TAG, "Activating next available sensor(s)");
//            // If there are no sensors to activate exit
//            if (availableSensorTypes.size() == 0) {
//                return;
//            }
//            // If there is only one activate that one if not activated
//            if (availableSensorTypes.size() == 1) {
//                if (activeSensors.indexOf(availableSensorTypes.get(0))<0) {
//                    activateSensor(availableSensorTypes.get(0));
//                }
//                return;
//            }
//            // Find the active sensors position in the available sensors
//            int countFound = 0;
//            int lastFoundIndex = 0;
//            for (Integer availableSensorType : availableSensorTypes) {
//                if (activeSensors.indexOf(availableSensorType) < 0) {
//                    continue;
//                }
//                // Found one
//                countFound += 1;
//                // If we found all
//                if (countFound == maxActiveSensors) {
//                    // Get the index that the last was found
//                    lastFoundIndex = availableSensorTypes.indexOf(availableSensorType);
//                    // Stop we don't need to loop more
//                    break;
//                }
//            }
//            // Deactivate all sensors
//            deactivateAllSensors();
//            // Enable the next ones (+1)
//            for (int i = 0; i < maxActiveSensors; i++) {
//                // Check if we hit the last
//                lastFoundIndex += 1;
//                if (lastFoundIndex >= availableSensorTypes.size()) {
//                    // Reset the index to start
//                    lastFoundIndex = 0;
//                }
//                // Activate
//                activateSensor(availableSensorTypes.get(lastFoundIndex));
//            }
//        }


        /**
         * Updates the sunrise and sunset according to a location if possible
         */
        private void updateSunriseAndSunset(@NonNull Location location) {
            Pair<String, String> sunriseSunset = SunriseSunsetHelper.getSunriseAndSunset(location, TimeZone.getDefault().getID());
            watchFace.updateSunriseSunset(sunriseSunset);
            invalidate();
            Log.d(TAG, "Successfully updated sunrise");
        }

        /**
         * Run's tasks according to the current time
         */
        private void runOnTimeTickTasks() {
            Calendar now = Calendar.getInstance();
            if (now.getTimeInMillis() - lastOnTimeTickTasksRun.getTimeInMillis() < RUN_ON_TICK_TASKS_EVERY_MS) {
                return;
            }
            Log.d(TAG, "Running onTimeTickTasks");
            watchFace.runTasks();
            lastOnTimeTickTasksRun = now;


            if (EmulatorHelper.isEmulator()) {
                Location location = new Location("dummy");
                location.setLatitude(41);
                location.setLongitude(11);
                location.setTime(System.currentTimeMillis());
                location.setAccuracy(3.0f);
                updateSunriseAndSunset(location);
//                deactivateAllSensors();
//                watchFace.addSensorColumn(Sensor.TYPE_HEART_RATE);
//                watchFace.updateSensorText(Sensor.TYPE_HEART_RATE, "128");
            }

            //calculateAverageForActiveSensors();
        }
    }
}
