package com.dimitrioskanellopoulos.athletica.paints;


import android.support.annotation.NonNull;

import com.dimitrioskanellopoulos.athletica.paints.interfaces.SensorPaintInterface;

public class SensorPaint extends TextPaint implements SensorPaintInterface {

    private final static String icon = "\uf1db";
    private final static String units = "";

    @Override
    public String getText() {
        if (super.getText() == null){
            return null;
        }
        return getIcon() + " " + super.getText() + " " + getUnits();
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public String getUnits() {
        return units;
    }
}
