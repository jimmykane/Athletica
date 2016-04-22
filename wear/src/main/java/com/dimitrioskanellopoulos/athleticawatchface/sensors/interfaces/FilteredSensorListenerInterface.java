package com.dimitrioskanellopoulos.athleticawatchface.sensors.interfaces;

import android.hardware.SensorEvent;

public interface FilteredSensorListenerInterface {
    Boolean isEventValueAcceptable(SensorEvent event);
}
