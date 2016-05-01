package com.dimitrioskanellopoulos.athletica.sensors.listeners;

import android.hardware.TriggerEvent;
import android.hardware.TriggerEventListener;

import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorTriggerCallbackInterface;

public class TriggerSensorEventListener extends TriggerEventListener {
    protected final static String TAG = TriggerSensorEventListener.class.getName();

    private OnSensorTriggerCallbackInterface triggerCallback;

    public TriggerSensorEventListener(OnSensorTriggerCallbackInterface triggerCallback) {
        this.triggerCallback = triggerCallback;
    }

    @Override
    public void onTrigger(TriggerEvent event) {
        triggerCallback.handleOnSensorTriggerEvent(event.sensor, event.sensor.getType(), event.values);
    }
}
