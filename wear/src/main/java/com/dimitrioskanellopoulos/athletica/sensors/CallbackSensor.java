package com.dimitrioskanellopoulos.athletica.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.util.Log;

import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorEventCallbackInterface;
import com.dimitrioskanellopoulos.athletica.sensors.interfaces.SensorListenerInterface;
import com.dimitrioskanellopoulos.athletica.sensors.listeners.ContinuousSensorEventListener;

public class CallbackSensor implements SensorListenerInterface, OnSensorEventCallbackInterface {
    public final static int TYPE_PRESSURE_ALTITUDE = 10000;
    public final static Integer samplingPeriodUs = 1000000;
    public final static Integer maxReportLatencyUs = 1000000;
    protected final String TAG;
    protected final SensorManager sensorManager;
    protected final Sensor sensor;
    private final OnSensorEventCallbackInterface changeCallback;
    private final ContinuousSensorEventListener continuousSensorEventListener = new ContinuousSensorEventListener(this);
    protected Boolean isListening = false;

    public CallbackSensor(@NonNull Context context, Integer sensorType,
                          @NonNull OnSensorEventCallbackInterface changeCallback) {
        this.changeCallback = changeCallback;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(sensorType);
        TAG = sensor.getStringType();
    }

    @Override
    public Boolean isEventValuesAcceptable(float[] eventValues) {
        return true;
    }

    @Override
    public void startListening() {
        // First stop listening
        stopListening();
        isListening = true;
        sensorManager.registerListener(continuousSensorEventListener, sensor, samplingPeriodUs, maxReportLatencyUs);
        Log.d(TAG, "Started listening");
    }

    @Override
    public void stopListening() {
        isListening = false;
        sensorManager.unregisterListener(continuousSensorEventListener);
        Log.d(TAG, "Stopped listening");
    }

    @Override
    public Boolean isListening() {
        return isListening;
    }

    @Override
    public void handleOnSensorChangedEvent(Sensor sensor, Integer sensorType, float[] eventValues) {
        if (isEventValuesAcceptable(eventValues)) {
            changeCallback.handleOnSensorChangedEvent(sensor, sensorType, eventValues);
        }
    }
}