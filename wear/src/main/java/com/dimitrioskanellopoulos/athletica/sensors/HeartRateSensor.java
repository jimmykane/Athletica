package com.dimitrioskanellopoulos.athletica.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.support.annotation.NonNull;

public class HeartRateSensor extends AbstractCallbackSensor {
    private static final Integer sensorType = Sensor.TYPE_HEART_RATE;

    public HeartRateSensor(@NonNull Context context, @NonNull OnSensorEventCallback changeCallback) {
        super(context, sensorType, changeCallback);
    }

    @Override
    public Boolean isEventValueAcceptable(SensorEvent event) {
        if (event.values[0] == 0.0f){
            return false;
        }
        return super.isEventValueAcceptable(event);
    }
}