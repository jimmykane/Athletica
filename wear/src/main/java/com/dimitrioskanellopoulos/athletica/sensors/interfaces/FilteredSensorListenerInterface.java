package com.dimitrioskanellopoulos.athletica.sensors.interfaces;

import android.hardware.SensorEvent;

public interface FilteredSensorListenerInterface {
    Boolean isEventValuesAcceptable(float[] eventValues);
}
