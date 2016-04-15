package com.dimitrioskanellopoulos.athletica;

public class SensorPaint extends AbstractTextPaint {

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
}
