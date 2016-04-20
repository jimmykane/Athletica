package com.dimitrioskanellopoulos.athletica.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dimitrioskanellopoulos.athletica.sensors.interfaces.SensorAverageListenerInterface;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAveragingCallbackSensor extends AbstractCallbackSensor implements SensorAverageListenerInterface {

    public AbstractAveragingCallbackSensor(@NonNull Context context, Integer sensorType, @NonNull OnSensorEventCallback changeCallback) {
        super(context, sensorType, changeCallback);
    }

    @Override
    public void getAverage() {
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