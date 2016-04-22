package com.dimitrioskanellopoulos.athleticawatchface.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.support.annotation.NonNull;

import com.dimitrioskanellopoulos.athleticawatchface.sensors.interfaces.OnSensorAverageEventCallbackInterface;
import com.dimitrioskanellopoulos.athleticawatchface.sensors.interfaces.OnSensorEventCallbackInterface;

public class CallbackSensorFactory {
    public static AveragingCallbackSensor getCallbackSensor(@NonNull Context context, Integer sensorType, @NonNull OnSensorEventCallbackInterface changeCallback, @NonNull OnSensorAverageEventCallbackInterface averageChangeCallback) {
        AveragingCallbackSensor callbackSensor;
        switch (sensorType) {
            case Sensor.TYPE_PRESSURE:
                callbackSensor = new PressureSensor(context, sensorType, changeCallback, averageChangeCallback);
                break;
            case Sensor.TYPE_HEART_RATE:
                callbackSensor = new HeartRateSensor(context, sensorType, changeCallback, averageChangeCallback);
                break;
            case Sensor.TYPE_TEMPERATURE:
                callbackSensor = new TemperatureSensor(context, sensorType, changeCallback, averageChangeCallback);
                break;
            default:
                callbackSensor = new AveragingCallbackSensor(context, sensorType, changeCallback, averageChangeCallback);
        }
        return callbackSensor;
    }
}
