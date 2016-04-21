package com.dimitrioskanellopoulos.athletica.paints;


import com.dimitrioskanellopoulos.athletica.paints.interfaces.SensorPaintInterface;

public class SensorPaint extends AbstractTextPaint implements SensorPaintInterface {

    private final static String icon = "\uf1db";
    private final static String units = "";

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
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
