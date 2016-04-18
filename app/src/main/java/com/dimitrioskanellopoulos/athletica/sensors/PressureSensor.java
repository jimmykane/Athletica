package com.dimitrioskanellopoulos.athletica.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.support.annotation.NonNull;

public class PressureSensor extends AbstractCallbackSensor {
    private static final Integer sensorType = Sensor.TYPE_PRESSURE;

    public PressureSensor(@NonNull Context context, @NonNull OnSensorEventCallback changeCallback) {
        super(context, sensorType, changeCallback);
    }
}