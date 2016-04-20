package com.dimitrioskanellopoulos.athletica.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorAverageEventCallbackInterface;
import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorEventCallbackInterface;
import com.dimitrioskanellopoulos.athletica.sensors.interfaces.SensorAverageListenerInterface;
import com.dimitrioskanellopoulos.athletica.sensors.listeners.AveragingSensorEventListener;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAveragingCallbackSensor extends AbstractCallbackSensor implements SensorAverageListenerInterface, OnSensorEventCallbackInterface, OnSensorAverageEventCallbackInterface {
    private final static Integer samplingPeriodUs = 5000;
    private final static Integer maxReportLatencyUs = 5000;


    private final AveragingSensorEventListener averagingSensorEventListener = new AveragingSensorEventListener(this);

    protected final OnSensorAverageEventCallbackInterface averageChangeCallback;

    public AbstractAveragingCallbackSensor(@NonNull Context context,
           Integer sensorType,
           @NonNull OnSensorEventCallbackInterface changeCallback,
           @NonNull OnSensorAverageEventCallbackInterface averageChangeCallback
    ) {
        super(context, sensorType, changeCallback);
        this.averageChangeCallback = averageChangeCallback;
    }

    @Override
    public void getAverage() {
        if (isListening()){
            Log.d(TAG, "Average calculation aborted. Sensor is listening");
            return;
        }
        Log.d(TAG, "Starting average calculation");
        sensorManager.registerListener(averagingSensorEventListener, sensor, samplingPeriodUs, maxReportLatencyUs);
    }

    @Override
    public void handleOnSensorAverageChangedEvent(SensorEvent event) {
        Log.d(TAG, "Average calculated: " + String.format("%.01f", event.values[0]));
        sensorManager.unregisterListener(averagingSensorEventListener);
        Log.d(TAG, "Stopped listening");
        if (isEventValueAcceptable(event)) {
            averageChangeCallback.handleOnSensorAverageChangedEvent(event);
        }
    }
}