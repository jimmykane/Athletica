package com.dimitrioskanellopoulos.athletica.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.support.annotation.NonNull;

import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorEventCallbackInterface;

public class PressureSensor extends AbstractCallbackSensor {
    private static final Integer sensorType = Sensor.TYPE_PRESSURE;

    public PressureSensor(@NonNull Context context, @NonNull OnSensorEventCallbackInterface changeCallback) {
        super(context, sensorType, changeCallback);
    }
}
