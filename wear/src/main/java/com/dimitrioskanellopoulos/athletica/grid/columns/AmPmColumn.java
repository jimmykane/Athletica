package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class AmPmColumn extends CalendarColumn {
    private static final String TAG = "AmPmColumn";
    private static final Calendar CALENDAR = Calendar.getInstance();
    private final static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("a", Locale.getDefault());

    public AmPmColumn(Context context, Typeface paintTypeface, Float paintTextSize, int paintColor) {
        super(context, paintTypeface, paintTextSize, paintColor);
    }

    @Override
    public String getText() {
        CALENDAR.setTimeInMillis(System.currentTimeMillis());
        return TIME_FORMAT.format(CALENDAR.getTime());
    }

    @Override
    public void start() {
        Log.d(TAG, "Started");
    }

    @Override
    public void destroy() {
        Log.d(TAG, "Destroyed");
    }
}
