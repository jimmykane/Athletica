package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import com.dimitrioskanellopoulos.athletica.grid.columns.abstracts.CalendarColumn;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class AmPmColumn extends CalendarColumn {
    private static final String TAG = "AmPmColumn";
    private final static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("a", Locale.getDefault());

    public AmPmColumn(Context context, Typeface paintTypeface, Float paintTextSize, int paintColor) {
        super(context, paintTypeface, paintTextSize, paintColor);
    }

    @Override
    public void setIsVisible(Boolean isVisible) {
        super.setIsVisible(isVisible);
        TIME_FORMAT.setTimeZone(CALENDAR.getTimeZone());
    }

    @Override
    public String getText() {
        CALENDAR.setTimeInMillis(System.currentTimeMillis());
        return TIME_FORMAT.format(CALENDAR.getTime());
    }

    @Override
    public void destroy() {
        Log.d(TAG, "Destroyed");
    }
}
