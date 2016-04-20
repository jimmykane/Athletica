package com.dimitrioskanellopoulos.athletica.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dimitrioskanellopoulos.athletica.sensors.interfaces.SensorEventListenerInterface;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCallbackSensor implements SensorEventListenerInterface {
    protected final String TAG;

    protected final SensorManager sensorManager;
    protected final Sensor sensor;

    protected Boolean isListening = false;
    protected final OnSensorEventCallback changeCallback;

    public interface OnSensorEventCallback {
        void handleOnSensorChangedEvent(SensorEvent event);
        void handleOnSensorAverageChanged(SensorEvent event);
    }

    public AbstractCallbackSensor(@NonNull Context context, Integer sensorType, @NonNull OnSensorEventCallback changeCallback) {
        this.changeCallback = changeCallback;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(sensorType);
        TAG = sensor.getStringType();
    }

    @Override
    public Boolean isEventValueAcceptable(SensorEvent event) {
        return true;
    }

    @Override
    public void startListening() {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        isListening = true;
        Log.d(TAG, "Started listening");
    }

    @Override
    public void stopListening() {
        sensorManager.unregisterListener(this);
        isListening = false;
        Log.d(TAG, "Stopped listening");
    }

    @Override
    public Boolean isListening() {
        return isListening;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (isEventValueAcceptable(event)) {
            changeCallback.handleOnSensorChangedEvent(event);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "Accuracy changed");
    }
}