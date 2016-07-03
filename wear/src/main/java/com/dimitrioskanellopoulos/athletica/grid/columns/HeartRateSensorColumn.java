package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.content.Context;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.os.Vibrator;
import android.util.Log;

class HeartRateSensorColumn extends SensorColumn {
    private final static String TAG = "HeartRateSensorColumn";

    /**
     * Don't be kinky on this. It's the vibrating system service. Useful for haptic feedback
     */
    private final Vibrator vibrator;

    HeartRateSensorColumn(Context context, Typeface paintTypeface, Float paintTextSize, int paintColor, int sensorType, Boolean visible, Boolean ambientMode) {
        super(context, paintTypeface, paintTextSize, paintColor, sensorType);
        vibrator = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void handleOnSensorChangedEvent(Sensor sensor, Integer sensorType, float[] eventValues) {
        if (Math.round(eventValues[0]) > 180) {
            vibrator.vibrate(new long[]{0, 250, 500, 250, 100, 250, 50, 250, 50}, -1);
        }
        setText(decimalFormat.format(Math.round(eventValues[0])));
        Log.d(TAG, "Updated value for sensor: " + getText());
    }
}
