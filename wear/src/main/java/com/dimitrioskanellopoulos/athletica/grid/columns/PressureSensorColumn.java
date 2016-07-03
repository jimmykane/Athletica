package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.content.Context;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.util.Log;

class PressureSensorColumn extends SensorColumn {
    private final static String TAG = "PressureSensorColumn";

    PressureSensorColumn(Context context, Typeface paintTypeface, Float paintTextSize, int paintColor, int sensorType, Boolean visible, Boolean ambientMode) {
        super(context, paintTypeface, paintTextSize, paintColor, sensorType);
    }

    @Override
    public void handleOnSensorChangedEvent(Sensor sensor, Integer sensorType, float[] eventValues) {
        setText(decimalFormat.format(eventValues[0]));
        Log.d(TAG, "Updated value for sensor: " + getText());
    }
}
