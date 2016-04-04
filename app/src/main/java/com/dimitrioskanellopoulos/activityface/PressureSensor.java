package com.dimitrioskanellopoulos.activityface;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;

public class PressureSensor {
    private SensorManager sensorManager=null;
    private PressureSensor.Callback cb=null;
    private Float lastReading;

    public PressureSensor(Context context, PressureSensor.Callback cb) {
        this.cb=cb;
        sensorManager=(SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        sensorManager.registerListener(listener,
                sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE),
                SensorManager.SENSOR_DELAY_UI);
    }

    public void close() {
        sensorManager.unregisterListener(listener);
    }

    private void pressureChanged(Float pressureValue) {
        if (cb!=null) {
            cb.onPressureChanged(pressureValue);
        }
    }

    public interface Callback {
        void onPressureChanged(Float pressureValue);
    }

    private SensorEventListener listener = new SensorEventListener() {
        public void onSensorChanged(SensorEvent event) {
                pressureChanged(event.values[0]);
                lastReading = event.values[0];
                sensorManager.unregisterListener(listener);
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            // unused
        }
    };
}