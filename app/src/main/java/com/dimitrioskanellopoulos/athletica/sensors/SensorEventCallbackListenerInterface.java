package com.dimitrioskanellopoulos.athletica.sensors;

import android.hardware.SensorEventListener;

public interface SensorEventCallbackListenerInterface extends SensorEventListener {
    public void startListening();

    public void stopListening();

    public Boolean isListening();
}
