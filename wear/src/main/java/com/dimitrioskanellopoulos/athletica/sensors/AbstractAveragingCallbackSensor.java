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

public abstract class AbstractAveragingCallbackSensor extends AbstractCallbackSensor implements SensorAverageListenerInterface {

    public AbstractAveragingCallbackSensor(@NonNull Context context, Integer sensorType, @NonNull OnSensorEventCallback changeCallback) {
        super(context, sensorType, changeCallback);
    }

    @Override
    public void getAverage() {
    }
}