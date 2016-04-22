package com.dimitrioskanellopoulos.athletica.paints;

import java.util.Objects;

public class BatterySensorPaint extends SensorPaint {

    private final static String batteryEmptyIcon = "\uf244";
    private final static String batteryQuarterIcon = "\uf243";
    private final static String batteryHalfIcon = "\uf242";
    private final static String batteryThreeQuartersIcon = "\uf241";
    private final static String batteryFullIcon = "\uf240";
    private final static String units = "%";

    @Override
    public String getText() {
        if (text == null){
            return null;
        }
        return getIcon() + " " + text + "" + getUnits();
    }

    @Override
    public String getIcon() {
        if (text == null) {
            return batteryEmptyIcon;
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
        return icon;
    }

    @Override
    public String getUnits() {
        return units;
    }
}
