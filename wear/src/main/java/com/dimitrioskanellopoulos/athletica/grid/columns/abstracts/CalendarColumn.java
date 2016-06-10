package com.dimitrioskanellopoulos.athletica.grid.columns.abstracts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.util.Log;

import com.dimitrioskanellopoulos.athletica.grid.columns.Column;
import com.dimitrioskanellopoulos.athletica.grid.columns.interfaces.ReceiverColumnInterface;

import java.util.Calendar;
import java.util.TimeZone;

public abstract class CalendarColumn extends Column implements ReceiverColumnInterface {
    protected static final Calendar CALENDAR = Calendar.getInstance();
    private final static String TAG = "CalendarColumn";
    private static boolean hasRegisteredReceivers = false;

    /**
     * Broadcast receiver for updating the timezone
     */
    private final BroadcastReceiver timeZoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Setting timezone to " + TimeZone.getTimeZone(intent.getStringExtra("time-zone")));
            CALENDAR.setTimeZone(TimeZone.getTimeZone(intent.getStringExtra("time-zone")));
        }
    };

    public CalendarColumn(Context context, Typeface paintTypeface, Float paintTextSize, int paintColor, Boolean visible, Boolean ambientMode) {
        super(context, paintTypeface, paintTextSize, paintColor, visible, ambientMode);
    }

    @Override
    public void setIsVisible(Boolean isVisible) {
        super.setIsVisible(isVisible);
        if (isVisible) {
            if (!hasRegisteredReceivers()){
                registerReceivers();
            }
            // Update in case it changed and the receiver missed it
            CALENDAR.setTimeZone(TimeZone.getDefault());
        } else {
            if (hasRegisteredReceivers()){
               unRegisterReceivers();
            }
        }
    }

    @Override
    public void registerReceivers() {
        hasRegisteredReceivers = true;
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
        context.registerReceiver(timeZoneReceiver, filter);
        Log.d(TAG, "Registered receiver");
    }

    @Override
    public void unRegisterReceivers() {
        hasRegisteredReceivers = false;
        context.unregisterReceiver(timeZoneReceiver);
        Log.d(TAG, "Unregistered receiver");
    }

    @Override
    public Boolean hasRegisteredReceivers() {
        return hasRegisteredReceivers;
    }

    @Override
    public void destroy() {
        unRegisterReceivers();
        super.destroy();
    }
}
