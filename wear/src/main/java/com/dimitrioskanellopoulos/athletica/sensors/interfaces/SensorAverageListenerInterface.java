package com.dimitrioskanellopoulos.athletica.sensors.interfaces;

public interface SensorAverageListenerInterface extends FilteredSensorListenerInterface {
    void getAverage();
    Integer getNumberOfSamples();
}
