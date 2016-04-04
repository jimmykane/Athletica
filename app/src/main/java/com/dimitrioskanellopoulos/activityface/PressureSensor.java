package com.dimitrioskanellopoulos.activityface;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;

public class PressureSensor implements SensorEventListener{
    private SensorManager sensorManager;
    private Float lastReading;
    private PressureChangeCallback pressureChangeCallback;

    public interface PressureChangeCallback {
        void pressureValueChanged(Float pressureValue);
    }

    public PressureSensor(Context context, PressureSensor.PressureChangeCallback pressureChangeCallback) {
        this.pressureChangeCallback = pressureChangeCallback;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void startListening(){
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE), SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stopListening(){
        sensorManager.unregisterListener(this);
    }

    public Float getLastReading(){
        return lastReading;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Pass the value to the callback
        if (pressureChangeCallback !=null) {
            pressureChangeCallback.pressureValueChanged(event.values[0]);
        }
        // Update last reading
        lastReading = event.values[0];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}