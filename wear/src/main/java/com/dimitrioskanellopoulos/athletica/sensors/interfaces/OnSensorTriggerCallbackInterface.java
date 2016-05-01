package com.dimitrioskanellopoulos.athletica.sensors.interfaces;

import android.hardware.Sensor;

public interface OnSensorTriggerCallbackInterface {
    void handleOnSensorTriggerEvent(Sensor sensor, Integer sensorType, float[] eventValues);
}
