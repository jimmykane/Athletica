package com.dimitrioskanellopoulos.athletica.paints;


import android.support.annotation.NonNull;

import com.dimitrioskanellopoulos.athletica.paints.interfaces.SensorPaintInterface;

public class SensorPaint extends TextPaint implements SensorPaintInterface {

    private final static String icon = "\uf1db";
    private final static String units = "?";

    @Override
    public String getText() {
        String text = "-/-";
        if (super.getText() != null){
            text = super.getText();
        }
        return getIcon() + " " + text + " " + getUnits();
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
