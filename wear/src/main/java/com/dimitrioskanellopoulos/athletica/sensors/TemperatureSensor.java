package com.dimitrioskanellopoulos.athletica.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.support.annotation.NonNull;

import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorAverageEventCallbackInterface;
import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorEventCallbackInterface;

public class TemperatureSensor extends AveragingCallbackSensor {
    private static final Integer sensorType = Sensor.TYPE_TEMPERATURE;

    public TemperatureSensor(@NonNull Context context, @NonNull OnSensorEventCallbackInterface changeCallback, @NonNull OnSensorAverageEventCallbackInterface averageChangeCallback) {
        super(context, TemperatureSensor.sensorType, changeCallback, averageChangeCallback);
    }
}
