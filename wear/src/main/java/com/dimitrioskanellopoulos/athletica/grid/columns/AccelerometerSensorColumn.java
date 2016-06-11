package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.content.Context;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.util.Log;

import java.text.DecimalFormat;

class AccelerometerSensorColumn extends SensorColumn {
    private final static String TAG = "AccelerometerSensorColumn";
    private final DecimalFormat decimalFormat = new DecimalFormat("##");

    AccelerometerSensorColumn(Context context, Typeface paintTypeface, Float paintTextSize, int paintColor, int sensorType, Boolean visible, Boolean ambientMode) {
        super(context, paintTypeface, paintTextSize, paintColor, sensorType, visible, ambientMode);
    }

    @Override
    public void handleOnSensorChangedEvent(Sensor sensor, Integer sensorType, float[] eventValues) {
        setText(decimalFormat.format(eventValues[0]) + "." + decimalFormat.format(eventValues[1]) + "." + decimalFormat.format(eventValues[2]));
        Log.d(TAG, "Updated value for sensor: " +getText());
    }
}
