package com.dimitrioskanellopoulos.athletica.sensors;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;

import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorAverageEventCallbackInterface;
import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorEventCallbackInterface;

/**
 * This a camouflaged pressure sensor.
 * It initializes a normal pressure sensor but overrides the callbacks to add special values and type
 */
public class PressureAltitudeSensor extends AveragingCallbackSensor {
    private static final Integer sensorOriginalType = Sensor.TYPE_PRESSURE;
    private static final Integer sensorType = CallbackSensor.TYPE_PRESSURE_ALTITUDE;

    public PressureAltitudeSensor(@NonNull Context context,
                                  @NonNull OnSensorEventCallbackInterface changeCallback,
                                    @NonNull OnSensorAverageEventCallbackInterface averageChangeCallback) {
        super(context, PressureAltitudeSensor.sensorOriginalType, changeCallback, averageChangeCallback);
    }

    @Override
    public void handleOnSensorChangedEvent(Sensor sensor, Integer sensorType, float[] eventValues) {
        eventValues[0] = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, eventValues[0]);
        super.handleOnSensorChangedEvent(sensor, PressureAltitudeSensor.sensorType, eventValues);
    }

    @Override
    public void handleOnSensorAverageChangedEvent(Sensor sensor, Integer sensorType, float[] eventValues) {
        eventValues[0] = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, eventValues[0]);
        super.handleOnSensorAverageChangedEvent(sensor, PressureAltitudeSensor.sensorType, eventValues);
    }
}
