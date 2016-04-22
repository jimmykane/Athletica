package com.dimitrioskanellopoulos.Athletica.paints;

import java.util.Objects;

public class SunriseSunsetPaint extends AbstractTextPaint {

    private final static String sunIcon = "\uf185";
    private final static String moonIcon = "\uf186";

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        if (Objects.equals(text, "") || text == null) {
            return text;
        }
        String[] sunriseSunset = text.split("-");
        return sunIcon + " " + sunriseSunset[0] + "   " + moonIcon + " " + sunriseSunset[1];
    }
}
