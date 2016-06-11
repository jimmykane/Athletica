package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.content.Context;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.os.Vibrator;
import android.util.Log;

import com.dimitrioskanellopoulos.athletica.sensors.AveragingCallbackSensor;
import com.dimitrioskanellopoulos.athletica.sensors.CallbackSensorFactory;
import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorAverageEventCallbackInterface;
import com.dimitrioskanellopoulos.athletica.sensors.interfaces.OnSensorEventCallbackInterface;

import java.text.DecimalFormat;

class SensorColumn extends Column implements OnSensorEventCallbackInterface,
        OnSensorAverageEventCallbackInterface {
    private final static String TAG = "SensorColumn";
    final static DecimalFormat decimalFormat =  new DecimalFormat("#.#");
    private final AveragingCallbackSensor averagingCallbackSensor;

    SensorColumn(Context context, Typeface paintTypeface, Float paintTextSize, int paintColor, int sensorType, Boolean visible, Boolean ambientMode) {
        super(context, paintTypeface, paintTextSize, paintColor, visible, ambientMode);
        averagingCallbackSensor = CallbackSensorFactory.getCallbackSensor(context, sensorType, this, this);
    }

    @Override
    public void setIsVisible(Boolean isVisible) {
        Log.d(TAG, "Set is visible " + isVisible.toString());
        if (isVisible && !isInAmbientMode()) {
            averagingCallbackSensor.startListening();
        } else {
            averagingCallbackSensor.stopListening();
        }
        super.setIsVisible(isVisible);
    }

    @Override
    public void setAmbientMode(Boolean ambientMode) {
        if (!ambientMode && isVisible()) {
            averagingCallbackSensor.startListening();
        } else {
            averagingCallbackSensor.stopListening();
        }
        super.setAmbientMode(ambientMode);
    }

    @Override
    public void handleOnSensorAverageChangedEvent(Sensor sensor, Integer sensorType, float[] eventValues) {
        handleOnSensorChangedEvent(sensor, sensorType, eventValues);
    }

    @Override
    public void handleOnSensorChangedEvent(Sensor sensor, Integer sensorType, float[] eventValues) {
        setText(decimalFormat.format(Math.round(eventValues[0])));
        Log.d(TAG, "Updated value for sensor: " + sensorType + " " + eventValues[0]);
    }

    @Override
    public void destroy() {
        averagingCallbackSensor.stopListening();
        Log.d(TAG, "Destroyed");
    }

    @Override
    public void runTasks() {
        Log.d(TAG, "Running tasks");
        setText("\u21bb");
        averagingCallbackSensor.getAverage();
    }
}
