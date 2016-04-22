package com.dimitrioskanellopoulos.athleticawatchface.sensors;

import android.content.Context;
import android.support.annotation.NonNull;

import com.dimitrioskanellopoulos.athleticawatchface.sensors.interfaces.OnSensorEventCallbackInterface;

public class CallbackSensor extends AbstractCallbackSensor {
    public CallbackSensor(@NonNull Context context, Integer sensorType, @NonNull OnSensorEventCallbackInterface changeCallback) {
        super(context, sensorType, changeCallback);
    }
}
