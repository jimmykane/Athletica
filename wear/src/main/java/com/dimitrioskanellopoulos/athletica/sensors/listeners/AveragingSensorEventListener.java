package com.dimitrioskanellopoulos.athletica.sensors.listeners;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorAverageEventCallbackInterface;

import java.util.ArrayList;
import java.util.List;

public class AveragingSensorEventListener implements SensorEventListener {
    protected final static String TAG = AveragingSensorEventListener.class.getName();

    private List<Float> averageValues = new ArrayList<>();

    private Integer numberOfSamples = 10;

    private OnSensorAverageEventCallbackInterface changeCallback;

    public AveragingSensorEventListener(OnSensorAverageEventCallbackInterface changeCallback) {
        this.changeCallback = changeCallback;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // If there is space to add more averageValues add it and do nothing
        if (averageValues.size() < numberOfSamples) {
            averageValues.add(event.values[0]);
            Log.d(TAG, "Value [" + event.values[0] + "], Collected Values [" + averageValues.size() + "]");
            return;
        }

        Float sum = 0.0f;
        Integer i = 0;
        for (Float value : averageValues) {
            if (value == 0.0) {
                continue;
            }
            sum += value;
            i++;
        }

        Float average = 0.0f;
        if (sum > 0 && i > 0) {
            average = sum / i;
        }

        Log.d(TAG, "Total sum[" + sum + "], Average[" + average + "]");
        averageValues.clear();
        event.values[0] = average;
        changeCallback.handleOnSensorAverageChangedEvent(event.sensor, event.sensor.getType(), event.values);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
