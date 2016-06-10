package com.dimitrioskanellopoulos.athletica.sensors.interfaces;

import android.hardware.Sensor;

public interface OnSensorAverageEventCallbackInterface extends OnSensorEventCallbackInterface {
    void handleOnSensorAverageChangedEvent(Sensor sensor, Integer sensorType, float[] eventValues);
}
