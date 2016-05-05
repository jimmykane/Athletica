package com.dimitrioskanellopoulos.athletica.paints;

import android.support.annotation.NonNull;

import com.dimitrioskanellopoulos.athletica.paints.interfaces.SensorPaintInterface;

public class SensorPaint extends TextPaint implements SensorPaintInterface {

    private final static String icon = "\uf1db";
    private final static String units = "?";
    private TextPaint iconTextPaint;

    public SensorPaint(){
        setIconTextPaint(new TextPaint());
    }

    @Override
    public String getText() {
        String text = " -/- ";
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
    public void setIconTextPaint(TextPaint iconTextPaint) {
        this.iconTextPaint = iconTextPaint;
    }

    @Override
    public TextPaint getIconTextPaint() {
        return iconTextPaint;
    }

    @Override
    public void inAmbientMode(@NonNull Boolean inAmbient) {
        getIconTextPaint().inAmbientMode(inAmbient);
        super.inAmbientMode(inAmbient);
    }
}
