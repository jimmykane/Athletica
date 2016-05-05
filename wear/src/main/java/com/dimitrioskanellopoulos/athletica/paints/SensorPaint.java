package com.dimitrioskanellopoulos.athletica.paints;

import com.dimitrioskanellopoulos.athletica.paints.interfaces.SensorPaintInterface;

public class SensorPaint extends TextPaint implements SensorPaintInterface {

    private final static String icon = "\uf1db";
    private final static String units = "?";
    private IconTextPaint iconTextPaint;

    public SensorPaint(){
        setIconTextPaint(new IconTextPaint());
    }

    @Override
    public String getText() {
        String text = "-/-";
        if (super.getText() != null){
            text = super.getText();
        }
        return text + " " + getUnits();
    }

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public String getUnits() {
        return units;
    }

    @Override
    public void setIconTextPaint(IconTextPaint iconTextPaint) {
        this.iconTextPaint = iconTextPaint;
    }

    @Override
    public IconTextPaint getIconTextPaint() {
        return iconTextPaint;
    }
}
