package com.dimitrioskanellopoulos.athletica.sensors.listeners;

import android.content.Intent;
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
        if (event.values[0] == 0.0f){
            numberOfSamples -= 1;
            Log.d(TAG, "Value [" + event.values[0] + "] is not accepted");
            return;
        }
        // If there is space to add more averageValues add it and do nothing
        if (averageValues.size() < numberOfSamples) {
            averageValues.add(event.values[0]);
            Log.d(TAG, "Value [" + event.values[0] + "], Collected Values [" + averageValues.size()+"]");
            return;
        }

        Float sum = 0.0f;
        for (Float value : averageValues) {
            sum = sum + value;
        }

        event.values[0] = sum / averageValues.size();
        Log.d(TAG, "Total sum[" + sum +  "], Average[" + event.values[0]+ "]");
        averageValues.clear();
        changeCallback.handleOnSensorAverageChangedEvent(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
