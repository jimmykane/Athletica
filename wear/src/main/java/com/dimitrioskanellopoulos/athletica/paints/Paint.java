package com.dimitrioskanellopoulos.athletica.paints;

public class Paint extends AbstractTextPaint {
    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }
}
