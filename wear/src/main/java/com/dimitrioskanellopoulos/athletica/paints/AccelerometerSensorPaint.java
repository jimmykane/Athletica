package com.dimitrioskanellopoulos.athletica.paints;

public class AccelerometerSensorPaint extends SensorPaint {

    private final static String icon = "\uf00a";
    private final static String units = "m/s^2";

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public String getUnits() {
        return units;
    }
}
