package com.dimitrioskanellopoulos.athletica.sensors.interfaces;

import android.hardware.Sensor;

public interface OnSensorEventCallbackInterface {
    void handleOnSensorChangedEvent(Sensor sensor, Integer sensorType, float[] eventValues);
}
