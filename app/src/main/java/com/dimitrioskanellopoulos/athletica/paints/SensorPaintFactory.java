package com.dimitrioskanellopoulos.athletica.paints;

import android.hardware.Sensor;

public class SensorPaintFactory {
    public static AbstractSensorPaint getPaintForSensorType(Integer sensorType){
        AbstractSensorPaint sensorPaint;
        switch (sensorType) {
            case Sensor.TYPE_PRESSURE:
                sensorPaint = new PressureSensorPaint();
                break;
            case Sensor.TYPE_HEART_RATE:
                sensorPaint = new HeartRateSensorPaint();
                break;
            default:
                sensorPaint = new SensorPaint();
        }
        return sensorPaint;
    }
}
