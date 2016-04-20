package com.dimitrioskanellopoulos.athletica.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCallbackSensor implements SensorEventListenerInterface {
    private String TAG = "CallbackSensor";

    private final SensorManager sensorManager;
    private final Sensor sensor;

    private Boolean isListening = false;
    private final OnSensorEventCallback changeCallback;

    public interface OnSensorEventCallback {
        void handleOnSensorChangedEvent(SensorEvent event);
        void handleOnSensorAverageChanged(SensorEvent event);
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
    public void onSensorChanged(SensorEvent event) {
        // Pass the value to the callback
        changeCallback.handleOnSensorChangedEvent(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "Accuracy changed");
    }

    SensorEventListener averageListener = new SensorEventListener() {

        private List<Float> averageValues = new ArrayList<>();

        @Override
        public void onSensorChanged(SensorEvent event) {

            // If there is space to add more averageValues add it and do nothing
            if (averageValues.size() < 10) {
                averageValues.add(event.values[0]);
                Log.d(TAG, "Averaging value: " + event.values[0] + " total: " + averageValues.size());
                return;
            }

            Float sum = 0.0f;
            for (Float value: averageValues){
                sum = sum + value;
            }

            event.values[0] = sum/ averageValues.size();
            Log.d(TAG, "Total sum: " + sum +  " Average: " + event.values[0]);
            averageValues.clear();
            sensorManager.unregisterListener(this);
            changeCallback.handleOnSensorAverageChanged(event);
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };

}