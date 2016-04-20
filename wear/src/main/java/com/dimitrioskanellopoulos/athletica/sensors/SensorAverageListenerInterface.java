package com.dimitrioskanellopoulos.athletica.sensors;

import android.hardware.SensorEventListener;

public interface SensorAverageListenerInterface extends SensorEventListener {
    void getAverage();
}
