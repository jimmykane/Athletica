package com.dimitrioskanellopoulos.athletica.matrix.columns;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;

import com.dimitrioskanellopoulos.athletica.R;

public class BatteryIconColumn extends Column {

    private float batteryLevel = 0.0f;

    private String batteryEmptyIcon;
    private String batteryQuarterIcon;
    private String batteryHalfIcon;
    private String batteryThreeQuartersIcon;
    private String batteryFullIcon;

    private int iconDefaultColor;

    public BatteryIconColumn(Resources resources, Typeface paintTypeface, Float paintTextSize, int paintColor) {
        super(paintTypeface, paintTextSize, paintColor);
        batteryEmptyIcon = resources.getString(R.string.icon_battery_empty);
        batteryQuarterIcon =  resources.getString(R.string.icon_battery_one_quarter);
        batteryHalfIcon =  resources.getString(R.string.icon_battery_half);
        batteryThreeQuartersIcon =  resources.getString(R.string.icon_battery_three_quarters);
        batteryFullIcon =  resources.getString(R.string.icon_battery_full);
        iconDefaultColor = paintColor;
    }

    @Override
    public String getText() {
        String icon;
        getPaint().setColor(iconDefaultColor);
        if (batteryLevel > 80 && batteryLevel <= 100) {
            icon = batteryFullIcon;
        } else if (batteryLevel > 60 && batteryLevel <= 80) {
            icon = batteryThreeQuartersIcon;
        } else if (batteryLevel > 40 && batteryLevel <= 60) {
            icon = batteryHalfIcon;
        } else if (batteryLevel >= 20 && batteryLevel <= 40) {
            icon = batteryQuarterIcon;
        } else {
            if (!isInAmbientMode()) {
                if (getPaint().getColor() != Color.RED){
                    iconDefaultColor = getPaint().getColor();
                    getPaint().setColor(Color.RED);
                }
            }
            icon = batteryEmptyIcon;
        }
        return icon;
    }

    public void setBatteryLevel(float batteryLevel){
        this.batteryLevel = batteryLevel;
    }
}
