package com.dimitrioskanellopoulos.athletica.matrix.columns;

import android.hardware.Sensor;

import com.dimitrioskanellopoulos.athletica.sensors.CallbackSensor;

public class SensorColumnFactory {
    public static Column getIconColumnForSensorType(Integer sensorType) {
        Column sensorIconColumn = new Column();
        switch (sensorType) {
            case Sensor.TYPE_PRESSURE:
                sensorIconColumn.setText("\uF0C3");
                break;
            case CallbackSensor.TYPE_PRESSURE_ALTITUDE:
                sensorIconColumn.setText("\uF1FE");
                break;
            case Sensor.TYPE_HEART_RATE:
                sensorIconColumn.setText("\uF21E");
                break;
            case Sensor.TYPE_TEMPERATURE:
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                sensorIconColumn.setText("\uF080");
                break;
            case Sensor.TYPE_ACCELEROMETER:
                sensorIconColumn.setText("\uF00A");
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                sensorIconColumn.setText("\uF076");
                break;
            case Sensor.TYPE_LIGHT:
                sensorIconColumn.setText("\uF042");
                break;
            default:
                sensorIconColumn.setText("?");
        }
        return sensorIconColumn;
    }

    public static Column getUnitsColumnForSensorType(Integer sensorType) {
        Column sensorUnitsColumn = new Column();
        switch (sensorType) {
            case Sensor.TYPE_PRESSURE:
                sensorUnitsColumn.setText("hPa");
                break;
            case CallbackSensor.TYPE_PRESSURE_ALTITUDE:
                sensorUnitsColumn.setText("m");
                break;
            case Sensor.TYPE_HEART_RATE:
                sensorUnitsColumn.setText("bpm");
                break;
            case Sensor.TYPE_TEMPERATURE:
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                sensorUnitsColumn.setText("C");
                break;
            case Sensor.TYPE_ACCELEROMETER:
                sensorUnitsColumn.setText("m/s^2");
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                sensorUnitsColumn.setText("uT");
                break;
            case Sensor.TYPE_LIGHT:
                sensorUnitsColumn.setText("lux");
                break;
            default:
                sensorUnitsColumn.setText("?");
        }
        return sensorUnitsColumn;
    }
}
