package com.dimitrioskanellopoulos.Athletica.sensors.interfaces;

import android.hardware.SensorEvent;

public interface OnSensorAverageEventCallbackInterface {
    void handleOnSensorAverageChangedEvent(SensorEvent event);
}
