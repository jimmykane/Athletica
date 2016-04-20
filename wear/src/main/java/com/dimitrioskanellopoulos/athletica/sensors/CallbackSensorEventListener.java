package com.dimitrioskanellopoulos.athletica.sensors;

import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

public interface CallbackSensorEventListener extends SensorEventListener {
    void startListening();

    void stopListening();

    void getAverage(Long time);

    Boolean isListening();
}
