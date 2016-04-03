package com.dimitrioskanellopoulos.activityface;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorService implements SensorEventListener {

    private SensorManager mSensorManager;
    private Sensor mSensor;


    @Override
    public void onSensorChanged(SensorEvent event) {
        // If sensor is unreliable, then just return
        if (event.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE)
        {
            return;
        }

        if(event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            String pressureText = Float.toString(event.values[0]);
            String altitudeText = Float.toString(SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, event.values[0]));
        }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
