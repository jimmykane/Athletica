package com.dimitrioskanellopoulos.athletica.sensors;

import android.content.Context;
import android.support.annotation.NonNull;

public class CallbackSensor extends AbstractCallbackSensor {
    public CallbackSensor(@NonNull Context context, Integer sensorType, @NonNull OnSensorEventCallback changeCallback) {
        super(context, sensorType, changeCallback);
    }
}