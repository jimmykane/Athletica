package com.dimitrioskanellopoulos.athletica.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.support.annotation.NonNull;

import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorAverageEventCallbackInterface;
import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorEventCallbackInterface;

public class PressureSensor extends AbstractAveragingCallbackSensor {
    private static final Integer sensorType = Sensor.TYPE_PRESSURE;

    public PressureSensor(@NonNull Context context, Integer sensorType, @NonNull OnSensorEventCallbackInterface changeCallback, @NonNull OnSensorAverageEventCallbackInterface averageChangeCallback) {
        super(context, sensorType, changeCallback, averageChangeCallback);
    }

}
