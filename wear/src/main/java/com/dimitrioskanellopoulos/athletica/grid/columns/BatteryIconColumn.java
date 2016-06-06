package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;

import com.dimitrioskanellopoulos.athletica.R;

public class BatteryIconColumn extends BatteryColumn {
    private static final String TAG = "BatteryIconColumn";

    private final String batteryEmptyIcon;
    private final String batteryQuarterIcon;
    private final String batteryHalfIcon;
    private final String batteryThreeQuartersIcon;
    private final String batteryFullIcon;

    public BatteryIconColumn(Context context, Typeface paintTypeface, Float paintTextSize, int paintColor) {
        super(context, paintTypeface, paintTextSize, paintColor);
        batteryEmptyIcon = this.context.getResources().getString(R.string.icon_battery_empty);
        batteryQuarterIcon = this.context.getResources().getString(R.string.icon_battery_one_quarter);
        batteryHalfIcon = this.context.getResources().getString(R.string.icon_battery_half);
        batteryThreeQuartersIcon = this.context.getResources().getString(R.string.icon_battery_three_quarters);
        batteryFullIcon = this.context.getResources().getString(R.string.icon_battery_full);
    }

    @Override
    public String getText() {
        String icon;
        getPaint().setColor(getTextDefaultColor());
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
                getPaint().setColor(Color.RED);
            }
            icon = batteryEmptyIcon;
        }
        return icon;
    }

    @Override
    public void destroy() {
        Log.d(TAG, "Destroyed");
    }
}
