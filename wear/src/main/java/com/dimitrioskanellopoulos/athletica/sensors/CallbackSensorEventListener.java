package com.dimitrioskanellopoulos.athletica.sensors;

import android.hardware.SensorEventListener;

public interface CallbackSensorEventListener extends SensorEventListener {
    public void startListening();

    public void stopListening();

    public Boolean isListening();
}
