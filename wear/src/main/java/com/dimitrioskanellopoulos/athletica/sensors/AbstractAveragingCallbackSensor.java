package com.dimitrioskanellopoulos.athletica.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorAverageEventCallbackInterface;
import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorEventCallbackInterface;
import com.dimitrioskanellopoulos.athletica.sensors.interfaces.SensorAverageListenerInterface;
import com.dimitrioskanellopoulos.athletica.sensors.listeners.AveragingSensorEventListener;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAveragingCallbackSensor extends AbstractCallbackSensor implements SensorAverageListenerInterface, OnSensorEventCallbackInterface, OnSensorAverageEventCallbackInterface {

    private final AveragingSensorEventListener averagingSensorEventListener = new AveragingSensorEventListener(this);

    public AbstractAveragingCallbackSensor(@NonNull Context context, Integer sensorType, @NonNull OnSensorEventCallbackInterface changeCallback, @NonNull OnSensorAverageEventCallbackInterface averageChangeCallback) {
        super(context, sensorType, changeCallback);
    }

    @Override
    public void getAverage() {

    }
}