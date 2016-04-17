package com.dimitrioskanellopoulos.athletica.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.util.Log;

public class CallbackSensor implements SensorEventListener {
    private String TAG = "CallbackSensor";

    private SensorManager sensorManager;
    private final Sensor sensor;

    private Boolean isListening = false;
    private onSensorEventCallback changeCallback;

    public interface onSensorEventCallback {
        void handleOnSensorChangedEvent(SensorEvent value);
    }

    public CallbackSensor(@NonNull Context context, Integer sensorType, @NonNull onSensorEventCallback changeCallback) {
        this.changeCallback = changeCallback;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(sensorType);
        TAG = TAG + ": " + sensor.getStringType();
    }

    public void startListening() {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        isListening = true;
        Log.d(TAG, "Started listening");
    }

    public void stopListening() {
        sensorManager.unregisterListener(this);
        isListening = false;
        Log.d(TAG, "Stopped listening");
    }

    public Boolean isListening() {
        return isListening;
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