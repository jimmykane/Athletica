package com.dimitrioskanellopoulos.athletica;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.util.Log;

public class CallbackSensor implements SensorEventListener {
    private static final String TAG = "CallbackSensor";

    private SensorManager sensorManager;
    private final Sensor sensor;

    private Boolean isListening = false;
    private changeCallback changeCallback;

    public interface changeCallback {
        void handleSensorValueChanged(Float value);
    }

    public CallbackSensor(@NonNull Context context, Integer sensorType, @NonNull changeCallback changeCallback) {
        this.changeCallback = changeCallback;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(sensorType);
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
        changeCallback.handleSensorValueChanged(event.values[0]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "Accuracy changed");
    }
}