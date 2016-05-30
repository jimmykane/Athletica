package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.content.Context;
import android.graphics.Typeface;

import java.text.DecimalFormat;

public class BatteryLevelColumn extends BatteryColumn {
    private static final String TAG = "BatteryIconColumn";

    public BatteryLevelColumn(Context context, Typeface paintTypeface, Float paintTextSize, int paintColor) {
        super(context, paintTypeface, paintTextSize, paintColor);
    }

    @Override
    public String getText() {
        DecimalFormat decimalFormat = new DecimalFormat("##");
        return decimalFormat.format(batteryLevel) + "%";
    }
}
