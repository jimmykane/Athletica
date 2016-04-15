package com.dimitrioskanellopoulos.athletica;

public class DatePaint extends AbstractTextPaint {
    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
}
