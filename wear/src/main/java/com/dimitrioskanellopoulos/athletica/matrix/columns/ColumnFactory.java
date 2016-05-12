package com.dimitrioskanellopoulos.athletica.matrix.columns;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.hardware.Sensor;

import com.dimitrioskanellopoulos.athletica.R;
import com.dimitrioskanellopoulos.athletica.sensors.CallbackSensor;

public class ColumnFactory {
    public static Column getIconColumnForSensorType(Resources resources, Integer sensorType, Typeface paintTypeface, Float paintTextSize, int paintColor) {
        Column sensorIconColumn = new Column(paintTypeface, paintTextSize, paintColor);
        switch (sensorType) {
            case Sensor.TYPE_PRESSURE:
                sensorIconColumn.setText(resources.getString(R.string.icon_android_sensor_pressure));
                break;
            case CallbackSensor.TYPE_PRESSURE_ALTITUDE:
                sensorIconColumn.setText(resources.getString(R.string.icon_android_sensor_pressure_altitude));
                break;
            case Sensor.TYPE_HEART_RATE:
                sensorIconColumn.setText(resources.getString(R.string.icon_android_sensor_heart_rate));
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                sensorIconColumn.setText(resources.getString(R.string.icon_android_sensor_ambient_temperature));
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                sensorIconColumn.setText(resources.getString(R.string.icon_android_sensor_magnetic_field));
                break;
            case Sensor.TYPE_LIGHT:
                sensorIconColumn.setText(resources.getString(R.string.icon_android_sensor_light));
                break;
            default:
                sensorIconColumn.setText("-/-");
        }
        return sensorIconColumn;
    }

    public static Column getUnitsColumnForSensorType(Resources resources, Integer sensorType, Typeface paintTypeface, Float paintTextSize, int paintColor) {
        Column sensorUnitsColumn = new Column(paintTypeface, paintTextSize, paintColor);
        switch (sensorType) {
            case Sensor.TYPE_PRESSURE:
                sensorUnitsColumn.setText(resources.getString(R.string.units_android_sensor_pressure));
                break;
            case CallbackSensor.TYPE_PRESSURE_ALTITUDE:
                sensorUnitsColumn.setText(resources.getString(R.string.units_android_sensor_pressure_altitude));
                break;
            case Sensor.TYPE_HEART_RATE:
                sensorUnitsColumn.setText(resources.getString(R.string.units_android_sensor_heart_rate));
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                sensorUnitsColumn.setText(resources.getString(R.string.units_android_sensor_ambient_temperature));
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                sensorUnitsColumn.setText(resources.getString(R.string.units_android_sensor_magnetic_field));
                break;
            case Sensor.TYPE_LIGHT:
                sensorUnitsColumn.setText(resources.getString(R.string.units_android_sensor_light));
                break;
            default:
                sensorUnitsColumn.setText("-/-");
        }
        return sensorUnitsColumn;
    }
}
