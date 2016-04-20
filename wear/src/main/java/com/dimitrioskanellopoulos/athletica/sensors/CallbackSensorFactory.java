package com.dimitrioskanellopoulos.athletica.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.support.annotation.NonNull;

import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorAverageEventCallbackInterface;
import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorEventCallbackInterface;

public class CallbackSensorFactory {
    public static AbstractCallbackSensor getCallbackSensor(@NonNull Context context, Integer sensorType, @NonNull OnSensorEventCallbackInterface changeCallback, @NonNull OnSensorAverageEventCallbackInterface averageChangeCallback) {
        AbstractCallbackSensor callbackSensor;
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
                callbackSensor = new CallbackSensor(context, sensorType, changeCallback);
        }
        return callbackSensor;
    }
}
