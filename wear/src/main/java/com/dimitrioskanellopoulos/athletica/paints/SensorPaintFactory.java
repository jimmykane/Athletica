package com.dimitrioskanellopoulos.athletica.paints;

import android.hardware.Sensor;

import com.dimitrioskanellopoulos.athletica.sensors.CallbackSensor;

public class SensorPaintFactory {
    public static SensorPaint getPaintForSensorType(Integer sensorType) {
        SensorPaint sensorPaint;
        switch (sensorType) {
            case Sensor.TYPE_PRESSURE:
                sensorPaint = new PressureSensorPaint();
                break;
            case CallbackSensor.TYPE_PRESSURE_ALTITUDE:
                sensorPaint = new PressureAltitudeSensorPaint();
                break;
            case Sensor.TYPE_HEART_RATE:
                sensorPaint = new HeartRateSensorPaint();
                break;
            case Sensor.TYPE_TEMPERATURE:
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                sensorPaint = new TemperatureSensorPaint();
                break;
            case Sensor.TYPE_ACCELEROMETER:
                sensorPaint = new AccelerometerSensorPaint();
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                sensorPaint = new MagneticFieldSensorPaint();
                break;
            case Sensor.TYPE_LIGHT:
                sensorPaint = new LightSensorPaint();
                break;
            default:
                sensorPaint = new SensorPaint();
        }
        return sensorPaint;
    }
}