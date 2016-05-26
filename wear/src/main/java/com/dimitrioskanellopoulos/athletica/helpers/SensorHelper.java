package com.dimitrioskanellopoulos.athletica.helpers;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;

import com.dimitrioskanellopoulos.athletica.sensors.CallbackSensor;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;

public class SensorHelper {
    private static final String TAG = "SensorHelper";

    private static final Integer[] FREE_VERSION_SENSORS = {
            Sensor.TYPE_HEART_RATE,
            Sensor.TYPE_MAGNETIC_FIELD,
            Sensor.TYPE_LIGHT,
            Sensor.TYPE_ACCELEROMETER,
    };

    private static final Integer[] PRO_VERSION_EXTRA_SENSORS = {
            Sensor.TYPE_PRESSURE,
            Sensor.TYPE_AMBIENT_TEMPERATURE,
            Sensor.TYPE_RELATIVE_HUMIDITY,
    };

    public static ArrayList<Integer> getApplicationDeviceSupportedSensors(Context context) {
        PackageManager packageManager = context.getApplicationContext().getPackageManager();
        ArrayList<Integer> applicationDeviceSupportedSensors = new ArrayList<>();

        Integer[] applicationSupportedSensors = PackageHelper.isPro(context) ? ArrayUtils.addAll(FREE_VERSION_SENSORS, PRO_VERSION_EXTRA_SENSORS) : FREE_VERSION_SENSORS;
        for (Integer applicationSupportedSensor : applicationSupportedSensors) {
            switch (applicationSupportedSensor) {
                case Sensor.TYPE_PRESSURE:
                    if (!packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_BAROMETER)) {
                        continue;
                    }
                    // Inject the altitude as a sensor
                    applicationDeviceSupportedSensors.add(CallbackSensor.TYPE_PRESSURE_ALTITUDE);
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
                case Sensor.TYPE_RELATIVE_HUMIDITY:
                    if (!packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_RELATIVE_HUMIDITY)) {
                        continue;
                    }
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    if (!packageManager.hasSystemFeature(PackageManager.FEATURE_SENSOR_ACCELEROMETER)) {
                        continue;
                    }
                    break;
                default:
                    continue;
            }
            applicationDeviceSupportedSensors.add(applicationSupportedSensor);
        }
        return applicationDeviceSupportedSensors;
    }
}
