package com.dimitrioskanellopoulos.athletica.sensors.interfaces;

public interface SensorEventListenerInterface extends FilteredSensorEventListenerInterface {
    void startListening();

    void stopListening();

    Boolean isListening();
}
