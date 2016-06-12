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
    // For continuous events
    private final static Integer samplingPeriodUs = SensorManager.SENSOR_DELAY_UI;
    private final static Integer maxReportLatencyUs = SensorManager.SENSOR_DELAY_UI; // 1 sec
    protected final String TAG;
    protected final Sensor sensor;
    final SensorManager sensorManager;
    private final OnSensorEventCallbackInterface changeCallback;
    private final ContinuousSensorEventListener continuousSensorEventListener = new ContinuousSensorEventListener(this);
    Boolean isListening = false;

    CallbackSensor(@NonNull Context context, Integer sensorType,
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
        if (isListening) {
            stopListening();
        }
        isListening = true;
        sensorManager.registerListener(continuousSensorEventListener, sensor, samplingPeriodUs, maxReportLatencyUs);
        Log.d(TAG, "Started listening");
    }

    @Override
    public void stopListening() {
        sensorManager.unregisterListener(continuousSensorEventListener);
        isListening = false;
        Log.d(TAG, "Stopped listening");
    }

    @Override
    public void handleOnSensorChangedEvent(Sensor sensor, Integer sensorType, float[] eventValues) {
        if (isEventValuesAcceptable(eventValues)) {
            changeCallback.handleOnSensorChangedEvent(sensor, sensorType, eventValues);
        }
    }
}