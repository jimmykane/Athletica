package com.dimitrioskanellopoulos.athletica;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.WindowInsets;

import com.dimitrioskanellopoulos.athletica.configuration.ConfigurationHelper;
import com.dimitrioskanellopoulos.athletica.helpers.EmulatorHelper;
import com.dimitrioskanellopoulos.athletica.sensors.CallbackSensor;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
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
     * How often the onTimeTick actions should run
     * 5 minutes for real device and 1 minute for emulator
     */
    private static final long RUN_ON_TICK_TASKS_EVERY_MS = !EmulatorHelper.isEmulator() ? 5 * 60 * 1000 : 1 * 60 * 1000;

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
         * When the onTickActions were run last time in ms
         */
        private Calendar lastOnTimeTickTasksRun = Calendar.getInstance();
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
         * The available sensors. Cross of supported by the app sensors and supported by the device
         */
        private ArrayList<Integer> availableSensorTypes = new ArrayList<>();

        @Override
        public void onCreate(SurfaceHolder holder) {
            super.onCreate(holder);

            // Set the style start with normal
            setWatchFaceStyle(watchFaceStyleNormal);

            // Create a watch face
            watchFace = new WatchFace(WatchFaceService.this);
            watchFace.setInAmbientMode(false);


            // Get a Google API client
            googleApiClient = new GoogleApiClient.Builder(WatchFaceService.this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(Wearable.API)
                    .build();
        }

        @Override
        public void onDestroy() {
            Log.d(TAG, "onDestroy");
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
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
            } else {
                if (googleApiClient != null && googleApiClient.isConnected()) {
                    Wearable.DataApi.removeListener(googleApiClient, this);
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
                    case ConfigurationHelper.KEY_SHOW_SUNRISE_SUNSET:
                        watchFace.showSunriseSunsetTimes(config.getBoolean(key));
                        break;
                    case ConfigurationHelper.KEY_INTERLACE:
                        watchFace.shouldInterlace(config.getBoolean(key));
                        break;
                    case ConfigurationHelper.KEY_INVERT_BLACK_AND_WHITE:
                        watchFace.setInvertBlackAndWhite(config.getBoolean(key));
                        setWatchFaceStyle(config.getBoolean(key) ? watchFaceStyleInverted : watchFaceStyleNormal);
                        break;
                    case ConfigurationHelper.KEY_DAY_NIGHT_MODE:
                        watchFace.setDayNightMode(config.getBoolean(key));
                        break;
                    case ConfigurationHelper.KEY_SHOW_GOOGLE_FIT_STEPS:
                        watchFace.showGoogleFitSteps(config.getBoolean(key));
                        break;
                    case ConfigurationHelper.KEY_ENABLED_SENSORS:
                        // Save new config
                        setAvailableSensorTypes(config.getIntegerArrayList(key));

                        // If new available sensor are none clear all
                        if (availableSensorTypes.size() == 0) {
                            watchFace.removeSensorsRow();
                            break;
                        }

                        Boolean found = false;
                        for (Integer availableSensorType : availableSensorTypes) {
                            if (watchFace.hasSensorColumn(availableSensorType)) {
                                found = true;
                            }
                        }
                        // If the current column was not found add next
                        if (!found) {
                            addSensorColumn();
                        }
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
                watchFace.removeSensorsRow();
                return;
            }
            // If only one
            if (availableSensorTypes.size() == 1) {
                // If it's not there
                if (!watchFace.hasSensorColumn(availableSensorTypes.get(0))) {
                    watchFace.removeSensorsRow();
                    watchFace.addSensorColumn(availableSensorTypes.get(0));
                }
                return;
            }
            // Check if found and what position and remove
            int indexFound = -1;
            int i = 0;
            for (Integer availableSensorType : availableSensorTypes) {
                if (watchFace.hasSensorColumn(availableSensorType)) {
                    indexFound = i;
                    break;
                }
                i++;
            }
            watchFace.removeSensorsRow();
            // If not found add 1st one
            if (indexFound == -1) {
                watchFace.addSensorColumn(availableSensorTypes.get(0));
                return;
            }
            // If last got to 1st
            if ((indexFound + 1) >= availableSensorTypes.size()) {
                watchFace.addSensorColumn(availableSensorTypes.get(0));
                return;
            }
            watchFace.addSensorColumn(availableSensorTypes.get(indexFound + 1));
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
        }
    }
}
