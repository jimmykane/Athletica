package com.dimitrioskanellopoulos.athleticawatchface.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dimitrioskanellopoulos.athleticawatchface.sensors.interfaces.OnSensorEventCallbackInterface;
import com.dimitrioskanellopoulos.athleticawatchface.sensors.interfaces.SensorListenerInterface;
import com.dimitrioskanellopoulos.athleticawatchface.sensors.listeners.ContinuousSensorEventListener;

public class AbstractCallbackSensor implements SensorListenerInterface, OnSensorEventCallbackInterface {
    protected final String TAG;

    protected final SensorManager sensorManager;
    protected final Sensor sensor;

    protected Boolean isListening = false;
    protected final OnSensorEventCallbackInterface changeCallback;

    private final ContinuousSensorEventListener continuousSensorEventListener = new ContinuousSensorEventListener(this);

    public AbstractCallbackSensor(@NonNull Context context, Integer sensorType, @NonNull OnSensorEventCallbackInterface changeCallback) {
        this.changeCallback = changeCallback;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(sensorType);
        TAG = sensor.getStringType();
    }

    @Override
    public Boolean isEventValueAcceptable(SensorEvent event) {
        return true;
    }

    @Override
    public void startListening() {
        sensorManager.registerListener(continuousSensorEventListener, sensor, SensorManager.SENSOR_DELAY_UI);
        isListening = true;
        Log.d(TAG, "Started listening");
    }

    @Override
    public void stopListening() {
        sensorManager.unregisterListener(continuousSensorEventListener);
        isListening = false;
        Log.d(TAG, "Stopped listening");
    }

    @Override
    public Boolean isListening() {
        return isListening;
    }

    @Override
    public void handleOnSensorChangedEvent(SensorEvent event) {
        if (isEventValueAcceptable(event)) {
            changeCallback.handleOnSensorChangedEvent(event);
        }
    }
}