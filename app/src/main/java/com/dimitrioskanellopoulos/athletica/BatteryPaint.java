package com.dimitrioskanellopoulos.athletica;

import java.util.Objects;

public class BatteryPaint extends AbstractTextPaint {

    String batteryEmptyIcon = "\uf244";
    String batteryQuarterIcon = "\uf243";
    String batteryHalfIcon = "\uf242";
    String batteryThreeQuartersIcon = "\uf241";
    String batteryFullIcon = "\uf240";

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        if (Objects.equals(text, "")){
            return text;
        }
        Integer batteryPercentage = Integer.parseInt(text);
        String icon;
        if (batteryPercentage > 80 && batteryPercentage <= 100) {
            icon = batteryFullIcon;
        } else if (batteryPercentage > 60 && batteryPercentage <= 80) {
            icon = batteryThreeQuartersIcon;
        } else if (batteryPercentage > 40 && batteryPercentage <= 60) {
            icon = batteryHalfIcon;
        } else if (batteryPercentage >= 20 && batteryPercentage <= 40) {
            icon = batteryQuarterIcon;
        } else {
            icon = batteryEmptyIcon;
        }
        return icon + " " + batteryPercentage + "%";
    }
}
