package com.dimitrioskanellopoulos.athletica.paints;

import java.util.Objects;

public class SunriseSunsetPaint extends TextPaint {

    private final static String sunIcon = "\uf185";
    private final static String moonIcon = "\uf186";

    @Override
    public String getText() {
        if (Objects.equals(super.getText(), "") || super.getText() == null) {
            return super.getText();
        }
        String[] sunriseSunset = super.getText().split("-");
        return sunIcon + " " + sunriseSunset[0] + "   " + moonIcon + " " + sunriseSunset[1];
    }
}
