package com.dimitrioskanellopoulos.Athletica.sensors;

import android.content.Context;
import android.support.annotation.NonNull;

import com.dimitrioskanellopoulos.Athletica.sensors.interfaces.OnSensorEventCallbackInterface;

public class CallbackSensor extends AbstractCallbackSensor {
    public CallbackSensor(@NonNull Context context, Integer sensorType, @NonNull OnSensorEventCallbackInterface changeCallback) {
        super(context, sensorType, changeCallback);
    }
}
