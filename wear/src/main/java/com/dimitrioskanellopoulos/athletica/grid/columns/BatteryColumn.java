package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.BatteryManager;
import android.util.Log;

public abstract class BatteryColumn extends Column {
    private static final String TAG = "BatteryColumn";

    protected static float batteryLevel = 0.0f;
    /**
     * Broadcast receiver for updating the battery level
     */
    private static final BroadcastReceiver batteryInfoReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            if (batteryStatus != null) {
                batteryLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            }
        }
    };
    protected static boolean isRegisteredBatteryInfoReceiver = false;

    public BatteryColumn(Context context, Typeface paintTypeface, Float paintTextSize, int paintColor) {
        super(context, paintTypeface, paintTextSize, paintColor);
    }

    @Override
    public void setIsVisible(Boolean isVisible) {
        super.setIsVisible(isVisible);
        if (isVisible) {
            registerBatteryInfoReceiver();
        } else {
            unregisterBatteryInfoReceiver();
        }

    }

    private void unregisterBatteryInfoReceiver() {
        if (!isRegisteredBatteryInfoReceiver) {
            return;
        }
        context.unregisterReceiver(batteryInfoReceiver);
        isRegisteredBatteryInfoReceiver = false;
        Log.d(TAG, "Unregistered receiver");
    }

    private void registerBatteryInfoReceiver() {
        if (isRegisteredBatteryInfoReceiver) {
            return;
        }
        context.registerReceiver(batteryInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        isRegisteredBatteryInfoReceiver = true;
        Log.d(TAG, "Registered receiver");

    }

    @Override
    public void destroy() {
        unregisterBatteryInfoReceiver();
        super.destroy();
    }
}
