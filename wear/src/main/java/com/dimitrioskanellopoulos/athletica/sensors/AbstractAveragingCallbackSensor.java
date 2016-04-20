package com.dimitrioskanellopoulos.athletica.sensors;

import android.content.Context;
import android.support.annotation.NonNull;

import com.dimitrioskanellopoulos.athletica.sensors.interfaces.SensorAverageListenerInterface;

public abstract class AbstractAveragingCallbackSensor extends AbstractCallbackSensor implements SensorAverageListenerInterface {

    public AbstractAveragingCallbackSensor(@NonNull Context context, Integer sensorType, @NonNull OnSensorEventCallback changeCallback) {
        super(context, sensorType, changeCallback);
    }

    @Override
    public void getAverage() {
    }
}