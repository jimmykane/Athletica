package com.dimitrioskanellopoulos.athleticawatchface.sensors.interfaces;

public interface SensorListenerInterface extends FilteredSensorListenerInterface {
    void startListening();

    void stopListening();

    Boolean isListening();
}
