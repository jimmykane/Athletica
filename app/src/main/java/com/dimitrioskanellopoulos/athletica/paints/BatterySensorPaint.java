package com.dimitrioskanellopoulos.athletica.paints;

import java.util.Objects;

public class BatterySensorPaint extends AbstractSensorPaint {

    private String batteryEmptyIcon = "\uf244";
    private String batteryQuarterIcon = "\uf243";
    private String batteryHalfIcon = "\uf242";
    private String batteryThreeQuartersIcon = "\uf241";
    private String batteryFullIcon = "\uf240";
    private String units = "%";

    @Override
    public String getIcon() {
        if (Objects.equals(text, "")){
            return text;
        }
        Integer batteryPercentage = Integer.parseInt(text);
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
    public String getUnits(){
        return units;
    }
}
