package com.dimitrioskanellopoulos.athletica.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorAverageEventCallbackInterface;
import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorEventCallbackInterface;

class HeartRateSensor extends AveragingCallbackSensor {
    private static final Integer sensorType = Sensor.TYPE_HEART_RATE;

    private final static Integer NUMBER_OF_SAMPLES = 1 ; // Due to doze?

    HeartRateSensor(@NonNull Context context,
                           @NonNull OnSensorEventCallbackInterface changeCallback,
                           @NonNull OnSensorAverageEventCallbackInterface averageChangeCallback) {
        super(context, HeartRateSensor.sensorType, changeCallback, averageChangeCallback);
    }

    @Override
    public Boolean isEventValuesAcceptable(float[] eventValues) {
        if (eventValues[0] == 0.0f) {
            Log.d(TAG, "Event value was 0 thus not accepted");
            return false;
        }
        Log.d(TAG, "Event value " + eventValues[0] + " accepted");
        return super.isEventValuesAcceptable(eventValues);
    }

    @Override
    public Integer getNumberOfSamples() {
        return NUMBER_OF_SAMPLES;
    }
}
