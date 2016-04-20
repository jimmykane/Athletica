package com.dimitrioskanellopoulos.athletica.sensors.interfaces;

public interface SensorListenerInterface extends FilteredSensorListenerInterface {
    void startListening();

    void stopListening();

    Boolean isListening();
}
