package com.dimitrioskanellopoulos.athletica.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorAverageEventCallbackInterface;
import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorEventCallbackInterface;

public class HeartRateSensor extends AveragingCallbackSensor {
    private static final Integer sensorType = Sensor.TYPE_HEART_RATE;

    public HeartRateSensor(@NonNull Context context, @NonNull OnSensorEventCallbackInterface changeCallback, @NonNull OnSensorAverageEventCallbackInterface averageChangeCallback) {
        super(context, HeartRateSensor.sensorType, changeCallback, averageChangeCallback);
    }

    @Override
    public Boolean isEventValuesAcceptable(float[] eventValues) {
        if (eventValues[0] == 0.0f){
            Log.d(TAG, "Event value was 0 thus not accepted");
            return false;
        }
        return super.isEventValuesAcceptable(eventValues);
    }
}
