package com.dimitrioskanellopoulos.athletica.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.support.annotation.NonNull;

public class CallbackSensorFactory {
    public static AbstractCallbackSensor getCallbackSensor(@NonNull Context context, Integer sensorType, @NonNull AbstractCallbackSensor.OnSensorEventCallback changeCallback) {
        AbstractCallbackSensor callbackSensor;
        switch (sensorType) {
            case Sensor.TYPE_PRESSURE:
                callbackSensor = new PressureSensor(context, changeCallback);
                break;
            case Sensor.TYPE_HEART_RATE:
                callbackSensor = new HeartRateSensor(context, changeCallback);
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                callbackSensor = new TemperatureSensor(context, changeCallback);
                break;
            default:
                callbackSensor = new CallbackSensor(context, sensorType, changeCallback);
        }
        return callbackSensor;
    }
}
