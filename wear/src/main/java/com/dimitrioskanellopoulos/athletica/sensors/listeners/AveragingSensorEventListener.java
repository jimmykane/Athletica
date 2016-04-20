package com.dimitrioskanellopoulos.athletica.sensors.listeners;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import com.dimitrioskanellopoulos.athletica.sensors.AbstractAveragingCallbackSensor;
import com.dimitrioskanellopoulos.athletica.sensors.AbstractCallbackSensor;
import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorAverageEventCallbackInterface;

import java.util.ArrayList;
import java.util.List;

public class AveragingSensorEventListener implements SensorEventListener {
    protected final static String TAG = AveragingSensorEventListener.class.getName();

    private List<Float> averageValues = new ArrayList<>();

    private OnSensorAverageEventCallbackInterface changeCallback;

    public AveragingSensorEventListener(OnSensorAverageEventCallbackInterface changeCallback) {
        this.changeCallback = changeCallback;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // If there is space to add more averageValues add it and do nothing
        if (averageValues.size() < 10) {
            averageValues.add(event.values[0]);
            //Log.d(TAG, "Averaging value: " + event.values[0] + " total: " + averageValues.size());
            return;
        }

        Float sum = 0.0f;
        for (Float value : averageValues) {
            sum = sum + value;
        }

        event.values[0] = sum / averageValues.size();
        //Log.d(TAG, "Total sum: " + sum +  " Average: " + event.values[0]);
        averageValues.clear();
        changeCallback.handleOnSensorAverageChangedEvent(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
