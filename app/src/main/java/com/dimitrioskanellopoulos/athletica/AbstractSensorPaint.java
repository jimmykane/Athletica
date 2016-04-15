package com.dimitrioskanellopoulos.athletica;

abstract class AbstractSensorPaint extends AbstractTextPaint {

    public AbstractSensorPaint(){

    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
}
