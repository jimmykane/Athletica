package com.dimitrioskanellopoulos.athletica.helpers;

import android.hardware.Sensor;

public class SensorHelper {
    private static final String TAG = "SensorHelper";
    private static final Integer[] SUPPORTED_SENSORS = {
            Sensor.TYPE_PRESSURE,
            Sensor.TYPE_HEART_RATE,
            Sensor.TYPE_AMBIENT_TEMPERATURE,
            Sensor.TYPE_LIGHT,
            Sensor.TYPE_MAGNETIC_FIELD,
    };
}
