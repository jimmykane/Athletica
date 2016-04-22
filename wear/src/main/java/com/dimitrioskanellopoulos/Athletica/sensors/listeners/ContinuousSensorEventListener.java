package com.dimitrioskanellopoulos.Athletica.sensors.listeners;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;

import com.dimitrioskanellopoulos.Athletica.sensors.interfaces.OnSensorEventCallbackInterface;

public class ContinuousSensorEventListener implements SensorEventListener {
    protected final static String TAG = AveragingSensorEventListener.class.getName();

    private OnSensorEventCallbackInterface changeCallback;

    public ContinuousSensorEventListener(OnSensorEventCallbackInterface changeCallback) {
        this.changeCallback = changeCallback;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        changeCallback.handleOnSensorChangedEvent(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
