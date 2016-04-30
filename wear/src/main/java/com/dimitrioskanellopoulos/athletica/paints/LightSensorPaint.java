package com.dimitrioskanellopoulos.athletica.paints;

public class LightSensorPaint extends SensorPaint {

    private final static String icon = "\uf042";
    private final static String units = "lux";

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public String getUnits() {
        return units;
    }
}
