package com.dimitrioskanellopoulos.athletica;

import java.util.Objects;

public class SensorComboPaint extends AbstractTextPaint {

    public AbstractSensorPaint firstPaint;
    public AbstractSensorPaint secondPaint;

    public SensorComboPaint(AbstractSensorPaint firstPaint, AbstractSensorPaint secondPaint){
        this.firstPaint = firstPaint;
        this.secondPaint = secondPaint;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        if (Objects.equals(text, "") || text == null){
            return text;
        }
        // @todo // FIXME: 15/04/16
        //return firstPaint.getText() + "   " + secondPaint.getText();
        String[] sunriseSunset = text.split("div");
        return firstPaint.getIcon() + " " + sunriseSunset[0] + "   " + secondPaint.getIcon() + " " + sunriseSunset[1];
    }
}
