package com.dimitrioskanellopoulos.athletica.sensors.interfaces;

import android.hardware.SensorEvent;

public interface FilteredSensorListenerInterface {
    Boolean isEventValueAcceptable(SensorEvent event);
}
