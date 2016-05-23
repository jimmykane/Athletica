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

public class SensorColumn extends Column implements OnSensorEventCallbackInterface,
        OnSensorAverageEventCallbackInterface {
    private final static String TAG = "Sensor Column";
    private final AveragingCallbackSensor averagingCallbackSensor;
    /**
     * Don't be kinky on this. It's the vibrating system service. Useful for haptic feedback
     */
    private final Vibrator vibrator;

    public SensorColumn(Context context, Typeface paintTypeface, Float paintTextSize, int paintColor, int sensorType) {
        super(paintTypeface, paintTextSize, paintColor);
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        this.averagingCallbackSensor = CallbackSensorFactory.getCallbackSensor(context, sensorType, this, this);
    }

    @Override
    public void setIsVisible(Boolean isVisible) {
        super.setIsVisible(isVisible);
        if (isVisible()){
            averagingCallbackSensor.startListening();
        }else{
            averagingCallbackSensor.stopListening();
        }
    }

    @Override
    public void setAmbientMode(Boolean ambientMode) {
        super.setAmbientMode(ambientMode);
        if (!ambientMode){
            averagingCallbackSensor.startListening();
        }else{
            averagingCallbackSensor.stopListening();
        }

    }

    @Override
    public void handleOnSensorAverageChangedEvent(Sensor sensor, Integer sensorType, float[] eventValues) {
        handleOnSensorChangedEvent(sensor, sensorType, eventValues);
    }

    @Override
    public void handleOnSensorChangedEvent(Sensor sensor, Integer sensorType, float[] eventValues) {

        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        switch (sensorType) {
            case Sensor.TYPE_PRESSURE:
                setText(decimalFormat.format(eventValues[0]));
                break;
            case Sensor.TYPE_HEART_RATE:
                if (Math.round(eventValues[0]) > 180) {
                    vibrator.vibrate(new long[]{0, 250, 500, 250, 100, 250, 50, 250, 50}, -1);
                }
                setText(decimalFormat.format(Math.round(eventValues[0])));
                break;
            default:
                setText(decimalFormat.format(Math.round(eventValues[0])));
                break;
        }
        Log.d(TAG, "Updated value for sensor: " + sensorType + " " + eventValues[0]);
    }
}
