package com.dimitrioskanellopoulos.athletica.paints;

public class TemperatureSensorPaint extends SensorPaint {

    private final static String icon = "\uf080";
    private final static String units = "c";

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public String getUnits() {
        return units;
    }
}
