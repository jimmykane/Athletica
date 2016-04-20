package com.dimitrioskanellopoulos.athletica.paints;

public class TemperatureSensorPaint extends AbstractSensorPaint {

    private String icon = "\uf080";
    private String units = "c";

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public String getUnits() {
        return units;
    }
}
