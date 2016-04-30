package com.dimitrioskanellopoulos.athletica.paints;

public class PressureAltitudeSensorPaint extends SensorPaint {

    private final static String icon = "\uf1fe";
    private final static String units = "m";

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public String getUnits() {
        return units;
    }
}
