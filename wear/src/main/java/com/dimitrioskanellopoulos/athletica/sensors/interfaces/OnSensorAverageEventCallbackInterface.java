package com.dimitrioskanellopoulos.athletica.sensors.interfaces;

import android.hardware.SensorEvent;

public interface OnSensorAverageEventCallbackInterface {
    void handleOnSensorAverageChangedEvent(SensorEvent event);
}
