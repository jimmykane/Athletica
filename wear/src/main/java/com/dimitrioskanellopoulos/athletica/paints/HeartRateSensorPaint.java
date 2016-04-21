package com.dimitrioskanellopoulos.athletica.paints;

public class HeartRateSensorPaint extends SensorPaint {

    private String icon = "\uf21e";
    private String units = "bpm";

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public String getUnits() {
        return units;
    }
}
