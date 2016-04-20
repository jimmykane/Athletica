package com.dimitrioskanellopoulos.athletica.sensors.interfaces;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public interface FilteredSensorEventListenerInterface extends SensorEventListener {
    Boolean isEventValueAcceptable(SensorEvent event);
}
