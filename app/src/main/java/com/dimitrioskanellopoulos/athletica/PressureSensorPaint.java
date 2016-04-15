package com.dimitrioskanellopoulos.athletica;

import java.util.Objects;

public class PressureSensorPaint extends AbstractSensorPaint {

    private String icon = "\uf1fe";
    private String units = "m";

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public String getUnits(){
        return units;
    }
}
