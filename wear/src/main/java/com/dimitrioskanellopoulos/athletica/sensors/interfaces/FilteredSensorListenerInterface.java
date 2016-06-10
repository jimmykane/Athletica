package com.dimitrioskanellopoulos.athletica.sensors.interfaces;

interface FilteredSensorListenerInterface {
    Boolean isEventValuesAcceptable(float[] eventValues);
}
