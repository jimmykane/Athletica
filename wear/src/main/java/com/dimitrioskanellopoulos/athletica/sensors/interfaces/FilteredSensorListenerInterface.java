package com.dimitrioskanellopoulos.athletica.sensors.interfaces;

public interface FilteredSensorListenerInterface {
    Boolean isEventValuesAcceptable(float[] eventValues);
}
