package com.dimitrioskanellopoulos.athletica.paints;

public class PressureSensorPaint extends AbstractSensorPaint {

    private String icon = "\uf1fe";
    private String units = "m";

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public String getUnits() {
        return units;
    }
}
