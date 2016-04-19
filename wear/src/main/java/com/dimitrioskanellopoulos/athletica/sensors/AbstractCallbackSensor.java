package com.dimitrioskanellopoulos.athletica.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.ref.WeakReference;

public abstract class AbstractCallbackSensor implements CallbackSensorEventListener {
    private String TAG = "CallbackSensor";

    private final SensorManager sensorManager;
    private final Sensor sensor;

    private Boolean isListening = false;
    private final OnSensorEventCallback changeCallback;

    public interface OnSensorEventCallback {
        void handleOnSensorChangedEvent(SensorEvent value);
        void handleOnSensorAverageChanged(Float average);
    }

    public AbstractCallbackSensor(@NonNull Context context, Integer sensorType, @NonNull OnSensorEventCallback changeCallback) {
        this.changeCallback = changeCallback;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(sensorType);
        TAG = TAG + ": " + sensor.getStringType();
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
    public void getAverage(Integer time) {
        SensorEventListener mListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }

        };
        sensorManager.registerListener(mListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        isListening = true;
        Log.d(TAG, "Monitoring average");
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Pass the value to the callback
        changeCallback.handleOnSensorChangedEvent(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "Accuracy changed");
    }

}