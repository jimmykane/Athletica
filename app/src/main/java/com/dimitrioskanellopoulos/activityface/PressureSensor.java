package com.dimitrioskanellopoulos.activityface;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.Calendar;
import java.util.HashMap;

public class PressureSensor implements SensorEventListener{
    private static final String TAG = "PressureSensor";

    private SensorManager sensorManager;
    private Float lastReading;
    private PressureChangeCallback pressureChangeCallback;

    private HashMap pressureReadings = new HashMap();

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

    private void updatePressureReadingsMap(){
        // Get the 24hour and minute
        Calendar rightNow = Calendar.getInstance();
        int hour = rightNow.get(Calendar.HOUR_OF_DAY);
        int minute = rightNow.get(Calendar.MINUTE);

        // Every 5 minutes
        if ((minute%5) != 0){
            return;
        }

        // Construct a key
        int key = hour+minute;
        // Clear if it has more than 2 hours
        if (pressureReadings.size() >= 60){
            pressureReadings.clear();
        }

        // If it's not set before
        if (!pressureReadings.containsKey(key)) {
            pressureReadings.put(hour + minute, lastReading);
            Log.d(TAG, "Pressure added to map");
        }
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