package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.content.Context;
import android.graphics.Typeface;
import android.hardware.Sensor;

import com.dimitrioskanellopoulos.athletica.R;
import com.dimitrioskanellopoulos.athletica.sensors.CallbackSensor;

public class ColumnFactory {
    public static Column getIconColumnForSensorType(Context context, Integer sensorType, Typeface paintTypeface, Float paintTextSize, int paintColor, Boolean visible, Boolean ambientMode) {
        Column sensorIconColumn = new Column(context, paintTypeface, paintTextSize, paintColor, visible, ambientMode);
        switch (sensorType) {
            case Sensor.TYPE_PRESSURE:
                sensorIconColumn.setText(context.getResources().getString(R.string.icon_android_sensor_pressure));
                break;
            case CallbackSensor.TYPE_PRESSURE_ALTITUDE:
                sensorIconColumn.setText(context.getResources().getString(R.string.icon_android_sensor_pressure_altitude));
                break;
            case Sensor.TYPE_HEART_RATE:
                sensorIconColumn.setText(context.getResources().getString(R.string.icon_android_sensor_heart_rate));
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                sensorIconColumn.setText(context.getResources().getString(R.string.icon_android_sensor_ambient_temperature));
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                sensorIconColumn.setText(context.getResources().getString(R.string.icon_android_sensor_magnetic_field));
                break;
            case Sensor.TYPE_LIGHT:
                sensorIconColumn.setText(context.getResources().getString(R.string.icon_android_sensor_light));
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                sensorIconColumn.setText(context.getResources().getString(R.string.icon_android_sensor_relative_humidity));
                break;
            case Sensor.TYPE_ACCELEROMETER:
                sensorIconColumn.setText(context.getResources().getString(R.string.icon_android_sensor_accelerometer));
                break;
            default:
                sensorIconColumn.setText("?");
        }
        return sensorIconColumn;
    }

    public static Column getColumnForSensorType(Context context, Integer sensorType, Typeface paintTypeface, Float paintTextSize, int paintColor, Boolean visible, Boolean ambientMode) {
        SensorColumn sensorColumn;
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
                sensorColumn = new AccelerometerSensorColumn(context, paintTypeface, paintTextSize, paintColor, sensorType, visible, ambientMode);
                break;
            case Sensor.TYPE_PRESSURE:
                sensorColumn = new PressureSensorColumn(context, paintTypeface, paintTextSize, paintColor, sensorType, visible, ambientMode);
                break;
            case Sensor.TYPE_HEART_RATE:
                sensorColumn = new HeartRateSensorColumn(context, paintTypeface, paintTextSize, paintColor, sensorType, visible, ambientMode);
                break;
            default:
                sensorColumn = new SensorColumn(context, paintTypeface, paintTextSize, paintColor, sensorType, visible, ambientMode);
                break;
        }
        return sensorColumn;
    }

    public static Column getUnitsColumnForSensorType(Context context, Integer sensorType, Typeface paintTypeface, Float paintTextSize, int paintColor, Boolean visible, Boolean ambientMode) {
        Column sensorUnitsColumn = new Column(context, paintTypeface, paintTextSize, paintColor, visible, ambientMode);
        switch (sensorType) {
            case Sensor.TYPE_PRESSURE:
                sensorUnitsColumn.setText(context.getResources().getString(R.string.units_android_sensor_pressure));
                break;
            case CallbackSensor.TYPE_PRESSURE_ALTITUDE:
                sensorUnitsColumn.setText(context.getResources().getString(R.string.units_android_sensor_pressure_altitude));
                break;
            case Sensor.TYPE_HEART_RATE:
                sensorUnitsColumn.setText(context.getResources().getString(R.string.units_android_sensor_heart_rate));
                break;
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                sensorUnitsColumn.setText(context.getResources().getString(R.string.units_android_sensor_ambient_temperature));
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                sensorUnitsColumn.setText(context.getResources().getString(R.string.units_android_sensor_magnetic_field));
                break;
            case Sensor.TYPE_LIGHT:
                sensorUnitsColumn.setText(context.getResources().getString(R.string.units_android_sensor_light));
                break;
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                sensorUnitsColumn.setText(context.getResources().getString(R.string.units_android_sensor_relative_humidity));
                break;
            case Sensor.TYPE_ACCELEROMETER:
                sensorUnitsColumn.setText(context.getResources().getString(R.string.units_android_sensor_accelerometer));
                break;
            default:
                sensorUnitsColumn.setText("?");
        }
        return sensorUnitsColumn;
    }
}
