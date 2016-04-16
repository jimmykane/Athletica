package com.dimitrioskanellopoulos.athletica;

import java.util.Objects;

public class SunriseSunsetPaint extends AbstractSensorPaint {

    private String sunIcon = "\uf185";
    private String moonIcon = "\uf186";

    @Override
    public String getIcon() {
        return icon;
    }

    @Override
    public String getUnits(){
        return units;
    }

    @Override
    public String getText() {
        if (Objects.equals(text, "") || text == null){
            return text;
        }
        String[] sunriseSunset = text.split("-");
        return sunIcon + " " + sunriseSunset[0] + "   " + moonIcon + " " +sunriseSunset[1];
    }
}
