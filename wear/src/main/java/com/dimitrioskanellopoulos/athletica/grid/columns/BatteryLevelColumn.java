package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.BatteryManager;
import android.util.Log;

import com.dimitrioskanellopoulos.athletica.R;

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
