package com.dimitrioskanellopoulos.athleticawatchface.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.support.annotation.NonNull;

import com.dimitrioskanellopoulos.athleticawatchface.sensors.interfaces.OnSensorAverageEventCallbackInterface;
import com.dimitrioskanellopoulos.athleticawatchface.sensors.interfaces.OnSensorEventCallbackInterface;

public class TemperatureSensor extends AveragingCallbackSensor {
    private static final Integer sensorType = Sensor.TYPE_TEMPERATURE;

    public TemperatureSensor(@NonNull Context context, Integer sensorType, @NonNull OnSensorEventCallbackInterface changeCallback, @NonNull OnSensorAverageEventCallbackInterface averageChangeCallback) {
        super(context, sensorType, changeCallback, averageChangeCallback);
    }
}
