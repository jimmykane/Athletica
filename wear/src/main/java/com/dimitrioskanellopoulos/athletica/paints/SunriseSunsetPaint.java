package com.dimitrioskanellopoulos.athletica.paints;

import java.util.Objects;

public class SunriseSunsetPaint extends TextPaint {

    private String sunIcon = "\uf185";
    private String moonIcon = "\uf186";

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
