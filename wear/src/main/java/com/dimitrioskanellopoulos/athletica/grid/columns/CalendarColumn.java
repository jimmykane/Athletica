package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.util.Log;

import java.util.Calendar;
import java.util.TimeZone;

public abstract class CalendarColumn extends Column {
    private final static String TAG = "CalendarColumn";
    private final Context context;


    protected static final Calendar CALENDAR = Calendar.getInstance();
    private static boolean isRegisteredTimeZoneReceiver = false;

    /**
     * Broadcast receiver for updating the timezone
     */
    private final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Setting timezone to " + TimeZone.getTimeZone(intent.getStringExtra("time-zone")));
            CALENDAR.setTimeZone(TimeZone.getTimeZone(intent.getStringExtra("time-zone")));
        }
    };

    public CalendarColumn(Context context, Typeface paintTypeface, Float paintTextSize, int paintColor) {
        super(paintTypeface, paintTextSize, paintColor);
        this.context = context.getApplicationContext();
    }

    @Override
    public void setIsVisible(Boolean isVisible) {
        super.setIsVisible(isVisible);
        if (isVisible) {
            registerTimeZoneReceiver();
            // Update in case it changed and the receiver missed it
            CALENDAR.setTimeZone(TimeZone.getDefault());
        }else {
            unregisterTimeZoneReceiver();
        }

    }

    private void registerTimeZoneReceiver() {
        if (isRegisteredTimeZoneReceiver) {
            return;
        }
        isRegisteredTimeZoneReceiver = true;
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
        context.registerReceiver(mTimeZoneReceiver, filter);
        Log.d(TAG, "Registered receiver");
    }

    private void unregisterTimeZoneReceiver() {
        if (!isRegisteredTimeZoneReceiver) {
            return;
        }
        isRegisteredTimeZoneReceiver = false;
        context.unregisterReceiver(mTimeZoneReceiver);
        Log.d(TAG, "Unregistered receiver");
    }

    @Override
    public void start() {
        super.start();
        // Maybe should register receiver
    }

    @Override
    public void destroy() {
        unregisterTimeZoneReceiver();
        super.destroy();
    }
}
