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
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.util.Log;
import android.util.Pair;
import android.view.SurfaceHolder;

import android.location.Location;
import android.view.WindowInsets;

import com.dimitrioskanellopoulos.athletica.sensors.AveragingCallbackSensor;
import com.dimitrioskanellopoulos.athletica.sensors.CallbackSensorFactory;
import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorAverageEventCallbackInterface;
import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorEventCallbackInterface;

import org.apache.commons.lang3.ArrayUtils;

import java.lang.ref.WeakReference;
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


    /**
     * The enabled activeSensors (activeSensors we want to display their values)
     */
    private int[] enabledSensorTypes = {
            Sensor.TYPE_HEART_RATE,
            Sensor.TYPE_PRESSURE,
            Sensor.TYPE_TEMPERATURE,
    };

    private static final int maxActiveSensors = 1;

    @Override
    public CanvasWatchFaceService.Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends CanvasWatchFaceService.Engine implements
            OnSensorEventCallbackInterface, OnSensorAverageEventCallbackInterface {

        private static final String TAG = "Engine";

        final Handler mUpdateTimeHandler = new EngineHandler(this);

        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                watchFace.updateTimeZoneWith(TimeZone.getTimeZone(intent.getStringExtra("time-zone")));
            }
        };

        final BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
                Intent batteryStatus = context.registerReceiver(null, ifilter);
                int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
                int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
                float batteryPct = level / (float) scale; // Used just in case
                watchFace.updateBatteryLevel(level);
            }
        };

        boolean mRegisteredTimeZoneReceiver = false;

        private WatchFace watchFace;

        private LocationEngine locationEngine;

        private GoogleApiHelper googleApiHelper;

        /**
         * Whether the display supports fewer bits for each color in ambient mode. When true, we
         * disable anti-aliasing in ambient mode.
         */
        boolean mLowBitAmbient;

        private final LinkedHashMap<Integer, AveragingCallbackSensor> activeSensors = new LinkedHashMap<Integer, AveragingCallbackSensor>();

        private final Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

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

            // Get a google api helper
            googleApiHelper = new GoogleApiHelper(WatchFaceService.this);

            // Get a location engine
            locationEngine = new LocationEngine(googleApiHelper);

            SensorManager mgr = (SensorManager) getSystemService(SENSOR_SERVICE);
            // Foreach of our enabled activeSensors check if the device has it and if not remove it
            for (int enabledSensorType : enabledSensorTypes) {
                if (mgr.getDefaultSensor(enabledSensorType) == null){
                    Log.d(TAG, "Removed unsupported sensor: " + enabledSensorType);
                    enabledSensorTypes = ArrayUtils.removeElement(enabledSensorTypes, enabledSensorType);
                }
            }

            // Activate the 1st sensor if available
            if (enabledSensorTypes.length > 0) {
                activateSensor(enabledSensorTypes[0]);
            }
        }

        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            googleApiHelper.disconnect();
            stopActiveSensors();
            super.onDestroy();
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (visible) {
                registerTimeZoneReceiver();
                registerBatteryInfoReceiver();
                updateSunriseAndSunset();
                startActiveSensors();

                // Update time zone in case it changed while we weren't visible.
                watchFace.updateTimeZoneWith(TimeZone.getDefault());
            } else {
                unregisterTimeZoneReceiver();
                unregisterBatteryInfoReceiver();
                stopActiveSensors();
            }
            // Whether the timer should be running depends on whether we're visible (as well as
            // whether we're in ambient mode), so we may need to start or stop the timer.
            updateTimer();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);

            watchFace.setAntiAlias(!inAmbientMode);
            watchFace.setShowSeconds(!isInAmbientMode());

            if (inAmbientMode) {
                stopActiveSensors();
            } else {
                startActiveSensors();
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
            checkActions();
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
                    // Go over the active sensors. Should be only one for now
                    Integer activeSensorType = enabledSensorTypes[0];
                    Integer nextSensorIndex = 0;
                    Integer activeSensorIndex = -1;
                    for (Map.Entry<Integer, AveragingCallbackSensor> entry : activeSensors.entrySet()) {
                        activeSensorIndex = ArrayUtils.indexOf(enabledSensorTypes, entry.getKey());
                        // If found break the loop
                        if (activeSensorIndex != -1){
                            activeSensorType = entry.getKey();
                            break;
                        }
                    }
                    // If it was the last in the list get the first
                    if (activeSensorIndex != enabledSensorTypes.length - 1) {
                        nextSensorIndex = activeSensorIndex + 1;
                    }
                    deactivateSensor(activeSensorType);
                    activateSensor(enabledSensorTypes[nextSensorIndex]);
                    startActiveSensors();
                    long[] pattern = {0, 50, 50, 50, 50, 100, 100};
                    vibrator.vibrate(pattern, -1);
                    break;

                case WatchFaceService.TAP_TYPE_TOUCH:
                    break;

                case WatchFaceService.TAP_TYPE_TOUCH_CANCEL:
                    break;
            }
        }

        @Override
        public void handleOnSensorChangedEvent(SensorEvent event) {
            switch (event.sensor.getType()) {
                case Sensor.TYPE_PRESSURE:
                    event.values[0] = locationEngine.getAltitudeFromPressure(event.values[0]);
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
            // Vibrate for 50 milliseconds
            vibrator.vibrate(50);
        }

        private void registerTimeZoneReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            WatchFaceService.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterTimeZoneReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            WatchFaceService.this.unregisterReceiver(mTimeZoneReceiver);
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

        private void activateSensor(Integer sensorType) {
            activeSensors.put(sensorType, CallbackSensorFactory.getCallbackSensor(getApplicationContext(), sensorType, this, this));
            watchFace.addSensorPaint(sensorType);
        }

        private void deactivateSensor(Integer sensorType) {
            activeSensors.get(sensorType).stopListening();
            activeSensors.remove(sensorType);
            watchFace.removeSensorPaint(sensorType);
        }

        private void updateSunriseAndSunset() {
            Location location = locationEngine.getLastKnownLocation();
            if (location == null) {
                // If its a real device continue to run
                // @todo solve this with timezone
                if (!isEmulator()) {
                    Log.e(TAG, "Could not update sunrise/sunset because no location was found");
                    return;
                }
                location = new Location("dummyprovider");
                location.setLatitude(20.3);
                location.setLongitude(52.6);
                location.setAltitude(650.0);
                location.setTime(System.currentTimeMillis());
                location.setAccuracy(40.0f);
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

        private void startActiveSensors() {
            for (Map.Entry<Integer, AveragingCallbackSensor> entry : activeSensors.entrySet()) {
                if (!entry.getValue().isListening()) {
                    //entry.getValue().getAverage(5000L);
                    entry.getValue().startListening();
                }
            }
        }

        private void calculateAverageForActiveSensors() {
            for (Map.Entry<Integer, AveragingCallbackSensor> entry : activeSensors.entrySet()) {
                if (!entry.getValue().isListening()) {
                    entry.getValue().getAverage();
                }
            }
        }

        private void stopActiveSensors() {
            for (Map.Entry<Integer, AveragingCallbackSensor> entry : activeSensors.entrySet()) {
                if (entry.getValue().isListening()) {
                    entry.getValue().stopListening();
                }
            }
        }

        private void checkActions() {
            Calendar rightNow = Calendar.getInstance();
            int hour = rightNow.get(Calendar.HOUR_OF_DAY);
            int minute = rightNow.get(Calendar.MINUTE);
            int second = rightNow.get(Calendar.SECOND);

            if (second == 0) {
                calculateAverageForActiveSensors();
            }
            // Everything happens at the first second every hour
            if (second != 0 || minute != 0) {
                return;
            }
            updateSunriseAndSunset();
        }
    }


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

    public final boolean isEmulator() {
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
