package com.dimitrioskanellopoulos.athletica.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorAverageEventCallbackInterface;
import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorEventCallbackInterface;
import com.dimitrioskanellopoulos.athletica.sensors.interfaces.SensorAverageListenerInterface;
import com.dimitrioskanellopoulos.athletica.sensors.listeners.AveragingSensorEventListener;

public class AveragingCallbackSensor extends CallbackSensor implements
        SensorAverageListenerInterface, OnSensorEventCallbackInterface,
        OnSensorAverageEventCallbackInterface {

    private final static Integer NUMBER_OF_SAMPLES = 10;

    // For averages
    private final static Integer AVERAGE_SAMPLING_PERIOD_US =  5000000; // 1 Sec
    private final static Integer AVERAGE_MAX_REPORT_LATENCY_US =  5000000; // 1 Sec;

    private final AveragingSensorEventListener averagingSensorEventListener;

    private final OnSensorAverageEventCallbackInterface averageChangeCallback;

    /**
     * Power manager needed to provide wakelocks
     */
    private final PowerManager powerManager;
    private final PowerManager.WakeLock wakeLock;

    AveragingCallbackSensor(@NonNull Context context,
                            Integer sensorType,
                            @NonNull OnSensorEventCallbackInterface changeCallback,
                            @NonNull OnSensorAverageEventCallbackInterface averageChangeCallback) {
        super(context, sensorType, changeCallback);
        averagingSensorEventListener = new AveragingSensorEventListener(getNumberOfSamples(), this, this);
        this.averageChangeCallback = averageChangeCallback;
        powerManager  = (PowerManager) context.getApplicationContext().getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                this.getClass().getName());
    }

    @Override
    public void getAverage() {
        Log.d(TAG, "Acquiring wake lock");
        wakeLock.acquire();
        if (isListening) {
            stopListening();
        }
        Log.d(TAG, "Starting average calculation");
        isListening = true;
        sensorManager.registerListener(averagingSensorEventListener, sensor, AVERAGE_SAMPLING_PERIOD_US, AVERAGE_MAX_REPORT_LATENCY_US);
    }

    @Override
    public Integer getNumberOfSamples() {
        return NUMBER_OF_SAMPLES;
    }

    @Override
    public void stopListening() {
        if (wakeLock.isHeld()) {
            Log.d(TAG, "Releasing wake lock");
            wakeLock.release();
        }
        sensorManager.unregisterListener(averagingSensorEventListener);
        super.stopListening();
    }

    @Override
    public void handleOnSensorAverageChangedEvent(Sensor sensor, Integer sensorType, float[] eventValues) {
        Log.d(TAG, "Releasing wake lock");
        wakeLock.release();
        stopListening();
        Log.d(TAG, "Average calculated: " + String.format("%.01f", eventValues[0]));
        if (isEventValuesAcceptable(eventValues)) {
            averageChangeCallback.handleOnSensorAverageChangedEvent(sensor, sensorType, eventValues);
        }
    }
}