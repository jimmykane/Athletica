package com.dimitrioskanellopoulos.activityface;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class PressureSensor implements SensorEventListener{
    private static final String TAG = "PressureSensor";

    private SensorManager sensorManager;
    private Float lastReading;
    private PressureChangeCallback pressureChangeCallback;

    public interface PressureChangeCallback {
        void handlePressureValueChanged(Float pressureValue);
    }

    public PressureSensor(Context context, PressureSensor.PressureChangeCallback pressureChangeCallback) {
        this.pressureChangeCallback = pressureChangeCallback;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void startListening(){
        Log.d(TAG, "Started listening");
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stopListening(){
        Log.d(TAG, "Stopped listening");
        sensorManager.unregisterListener(this);
    }

    public Float getLastReading(){
        return lastReading;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Pass the value to the callback
        if (pressureChangeCallback !=null) {
            pressureChangeCallback.handlePressureValueChanged(event.values[0]);
        }
        // Update last reading
        lastReading = event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "Accuracy changed");
    }
}