package com.dimitrioskanellopoulos.athletica.paints;

public class MagneticFieldSensorPaint extends SensorPaint {

    private final static String icon = "\uf076";
    private final static String units = "uT";

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public String getUnits() {
        return units;
    }
}
