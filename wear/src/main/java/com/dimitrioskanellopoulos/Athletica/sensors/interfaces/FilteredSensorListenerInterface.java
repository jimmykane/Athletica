package com.dimitrioskanellopoulos.Athletica.sensors.interfaces;

import android.hardware.SensorEvent;

public interface FilteredSensorListenerInterface {
    Boolean isEventValueAcceptable(SensorEvent event);
}
