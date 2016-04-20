package com.dimitrioskanellopoulos.athletica.sensors;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public interface SensorEventListenerInterface extends SensorEventListener {
    void startListening();

    void stopListening();

    Boolean isListening();
}
