package com.dimitrioskanellopoulos.athleticawatchface.paints;

import android.hardware.Sensor;

public class SensorPaintFactory {
    public static SensorPaint getPaintForSensorType(Integer sensorType) {
        SensorPaint sensorPaint;
        switch (sensorType) {
            case Sensor.TYPE_PRESSURE:
                sensorPaint = new PressureSensorPaint();
                break;
            case Sensor.TYPE_HEART_RATE:
                sensorPaint = new HeartRateSensorPaint();
                break;
            case Sensor.TYPE_TEMPERATURE:
                sensorPaint = new TemperatureSensorPaint();
                break;
            default:
                sensorPaint = new SensorPaint();
        }
        return sensorPaint;
    }
}