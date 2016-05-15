package com.dimitrioskanellopoulos.athletica.helpers;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;

import java.util.ArrayList;

public class SensorHelper {
    private static final String TAG = "SensorHelper";
    private static final Integer[] APPLICATION_SUPPORTED_SENSORS = {
            Sensor.TYPE_PRESSURE,
            Sensor.TYPE_HEART_RATE,
            Sensor.TYPE_AMBIENT_TEMPERATURE,
            Sensor.TYPE_LIGHT,
            Sensor.TYPE_MAGNETIC_FIELD,
    };

    private static Integer[] getApplicationDeviceSupportedSensors(Context context) {
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        ArrayList<Integer> applicationDeviceSupportedSensors = new ArrayList<>();
        for (Integer applicationSupportedSensor : APPLICATION_SUPPORTED_SENSORS) {
            switch (applicationSupportedSensor) {
                case Sensor.TYPE_PRESSURE:
                    if (!packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_BAROMETER)) {
                        continue;
                    }
                    break;
                case Sensor.TYPE_HEART_RATE:
                    if (!packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_HEART_RATE)) {
                        continue;
                    }
                    break;
                case Sensor.TYPE_AMBIENT_TEMPERATURE:
                    if (!packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_AMBIENT_TEMPERATURE)) {
                        continue;
                    }
                    break;
                case Sensor.TYPE_LIGHT:
                    if (!packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_LIGHT)) {
                        continue;
                    }
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    if (!packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS)) {
                        continue;
                    }
                    break;
                default:
                    continue;
            }
            applicationDeviceSupportedSensors.add(applicationSupportedSensor);

        }
        return applicationDeviceSupportedSensors.toArray(new Integer[0]);
    }
}
