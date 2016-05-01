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
    private final static Integer samplingPeriodUs = 1000000;
    private final static Integer maxReportLatencyUs = 1000000;


    private final AveragingSensorEventListener averagingSensorEventListener = new AveragingSensorEventListener(this);

    private final OnSensorAverageEventCallbackInterface averageChangeCallback;

    public AveragingCallbackSensor(@NonNull Context context,
                                   Integer sensorType,
                                   @NonNull OnSensorEventCallbackInterface changeCallback,
                                   @NonNull OnSensorAverageEventCallbackInterface averageChangeCallback) {
        super(context, sensorType, changeCallback);
        this.averageChangeCallback = averageChangeCallback;
    }

    @Override
    public void getAverage() {
        stopListening();
        Log.d(TAG, "Starting average calculation");
        sensorManager.registerListener(averagingSensorEventListener, sensor, samplingPeriodUs, maxReportLatencyUs);
        isListening = true;
    }

    @Override
    public void stopListening() {
        sensorManager.unregisterListener(averagingSensorEventListener);
        super.stopListening();
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