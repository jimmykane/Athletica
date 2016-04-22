package com.dimitrioskanellopoulos.athletica.sensors;

import android.content.Context;
import android.support.annotation.NonNull;

import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorEventCallbackInterface;

public class CallbackSensor extends AbstractCallbackSensor {
    public CallbackSensor(@NonNull Context context, Integer sensorType, @NonNull OnSensorEventCallbackInterface changeCallback) {
        super(context, sensorType, changeCallback);
    }
}
