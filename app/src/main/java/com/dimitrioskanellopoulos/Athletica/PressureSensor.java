package com.dimitrioskanellopoulos.athletica;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.TriggerEventListener;
import android.util.Log;

import java.util.Calendar;
import java.util.HashMap;

public class PressureSensor implements SensorEventListener{
    private static final String TAG = "PressureSensor";

    private SensorManager sensorManager;
    private final Sensor sensor;

    private Float lastReading;
    private changeCallback changeCallback;

    private HashMap pressureReadings = new HashMap();

    public interface changeCallback {
        void handleSensorValueChanged(Float value);
    }

    public PressureSensor(Context context, changeCallback changeCallback) {
        this.changeCallback = changeCallback;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
    }

    public void startListening(){
        Log.d(TAG, "Started listening");
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stopListening(){
        Log.d(TAG, "Stopped listening");
        sensorManager.unregisterListener(this);
    }

    public Float getLastReading(){
        return lastReading;
    }

    public Float getAverage(int seconds){

        return 0.0f;
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
        if (changeCallback !=null) {
            changeCallback.handleSensorValueChanged(event.values[0]);
        }
        // Update last reading
        lastReading = event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        Log.d(TAG, "Accuracy changed");
    }
}