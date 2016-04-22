package com.dimitrioskanellopoulos.athleticawatchface.paints;

public class PressureSensorPaint extends SensorPaint {

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
