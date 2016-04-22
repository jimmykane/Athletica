package com.dimitrioskanellopoulos.Athletica.sensors.interfaces;

public interface SensorListenerInterface extends FilteredSensorListenerInterface {
    void startListening();

    void stopListening();

    Boolean isListening();
}
