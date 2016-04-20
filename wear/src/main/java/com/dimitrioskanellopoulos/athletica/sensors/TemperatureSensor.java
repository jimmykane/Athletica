package com.dimitrioskanellopoulos.athletica.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.support.annotation.NonNull;

public class TemperatureSensor extends AbstractCallbackSensor {
    private static final Integer sensorType = Sensor.TYPE_TEMPERATURE;

    public TemperatureSensor(@NonNull Context context, @NonNull OnSensorEventCallback changeCallback) {
        super(context, sensorType, changeCallback);
    }
}