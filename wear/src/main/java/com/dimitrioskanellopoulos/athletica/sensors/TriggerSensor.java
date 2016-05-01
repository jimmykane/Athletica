package com.dimitrioskanellopoulos.athletica.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;

import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorTriggerCallbackInterface;
import com.dimitrioskanellopoulos.athletica.sensors.listeners.TriggerSensorEventListener;

public class TriggerSensor implements OnSensorTriggerCallbackInterface {
    protected final String TAG;

    protected final SensorManager sensorManager;
    protected final Sensor sensor;

    private final OnSensorTriggerCallbackInterface triggerCallback;

    private final TriggerSensorEventListener triggerSensorEventListener = new TriggerSensorEventListener(this);

    public TriggerSensor(@NonNull Context context, Integer sensorType,
                         @NonNull OnSensorTriggerCallbackInterface triggerCallback) {
        this.triggerCallback = triggerCallback;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(sensorType);
        TAG = sensor.getStringType();
        sensorManager.requestTriggerSensor(triggerSensorEventListener, sensor);
    }

    @Override
    public void handleOnSensorTriggerEvent(Sensor sensor, Integer sensorType, float[] eventValues) {
        triggerCallback.handleOnSensorTriggerEvent(sensor, sensorType, eventValues);
    }
}