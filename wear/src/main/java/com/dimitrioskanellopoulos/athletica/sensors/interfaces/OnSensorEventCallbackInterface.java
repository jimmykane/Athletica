package com.dimitrioskanellopoulos.athletica.sensors.interfaces;

import android.hardware.SensorEvent;

public interface OnSensorEventCallbackInterface {
    void handleOnSensorChangedEvent(SensorEvent event);
}
