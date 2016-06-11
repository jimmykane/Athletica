package com.dimitrioskanellopoulos.athletica.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorAverageEventCallbackInterface;
import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorEventCallbackInterface;
import com.dimitrioskanellopoulos.athletica.sensors.interfaces.SensorAverageListenerInterface;
import com.dimitrioskanellopoulos.athletica.sensors.listeners.AveragingSensorEventListener;

public class AveragingCallbackSensor extends CallbackSensor implements
        SensorAverageListenerInterface, OnSensorEventCallbackInterface,
        OnSensorAverageEventCallbackInterface {


    // For averages
    private final static Integer averageSamplingPeriodUs = 5000000; // 5 secs
    private final static Integer averageMaxReportLatencyUs = 5000000; // 5 secs


    private final AveragingSensorEventListener averagingSensorEventListener;

    private final OnSensorAverageEventCallbackInterface averageChangeCallback;

    public AveragingCallbackSensor(@NonNull Context context,
                            Integer sensorType,
                            @NonNull OnSensorEventCallbackInterface changeCallback,
                            @NonNull OnSensorAverageEventCallbackInterface averageChangeCallback) {
        super(context, sensorType, changeCallback);
        averagingSensorEventListener = new AveragingSensorEventListener(this, this);
        this.averageChangeCallback = averageChangeCallback;
    }

    @Override
    public void getAverage() {
        stopListening();
        Log.d(TAG, "Starting average calculation");
        isListening = true;
        sensorManager.registerListener(averagingSensorEventListener, sensor, averageSamplingPeriodUs, averageMaxReportLatencyUs);
    }

    @Override
    public void stopListening() {
        super.stopListening();
        sensorManager.unregisterListener(averagingSensorEventListener);
    }

    @Override
    public void handleOnSensorAverageChangedEvent(Sensor sensor, Integer sensorType, float[] eventValues) {
        stopListening();
        Log.d(TAG, "Average calculated: " + String.format("%.01f", eventValues[0]));
        if (isEventValuesAcceptable(eventValues)) {
            averageChangeCallback.handleOnSensorAverageChangedEvent(sensor, sensorType, eventValues);
        }
    }
}