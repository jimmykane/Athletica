package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.content.Context;
import android.graphics.Typeface;
import android.hardware.Sensor;

import com.dimitrioskanellopoulos.athletica.sensors.AveragingCallbackSensor;
import com.dimitrioskanellopoulos.athletica.sensors.CallbackSensorFactory;
import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorAverageEventCallbackInterface;
import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorEventCallbackInterface;

public class SensorColumn extends Column implements OnSensorEventCallbackInterface,
        OnSensorAverageEventCallbackInterface {
    private final AveragingCallbackSensor averagingCallbackSensor;

    public SensorColumn(Context context, Typeface paintTypeface, Float paintTextSize, int paintColor, int sensorType) {
        super(paintTypeface, paintTextSize, paintColor);
        this.averagingCallbackSensor = CallbackSensorFactory.getCallbackSensor(context, sensorType, this, this);
    }

    @Override
    public void handleOnSensorAverageChangedEvent(Sensor sensor, Integer sensorType, float[] eventValues) {
    }

    @Override
    public void handleOnSensorChangedEvent(Sensor sensor, Integer sensorType, float[] eventValues) {

    }
}
