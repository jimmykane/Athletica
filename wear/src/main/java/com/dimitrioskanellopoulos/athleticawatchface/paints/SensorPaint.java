package com.dimitrioskanellopoulos.athleticawatchface.paints;


import android.support.annotation.NonNull;

import com.dimitrioskanellopoulos.athleticawatchface.paints.interfaces.SensorPaintInterface;

public class SensorPaint extends AbstractTextPaint implements SensorPaintInterface {

    private final static String icon = "\uf1db";
    private final static String units = "";

    @Override
    public void setText(@NonNull String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        if (text == null){
            return null;
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
