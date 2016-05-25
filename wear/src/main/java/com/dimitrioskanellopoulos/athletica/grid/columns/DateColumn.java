package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.graphics.Typeface;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateColumn extends CalendarColumn {
    private final static String TAG = "DateColumn";

    private static final Calendar CALENDAR = Calendar.getInstance();
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private static final SimpleDateFormat DATE_NAMES_FORMAT = new SimpleDateFormat("E d LLL yyyy", Locale.getDefault());

    private Boolean showDateNamesFormat = false;

    public DateColumn(Typeface paintTypeface, Float paintTextSize, int paintColor) {
        super(paintTypeface, paintTextSize, paintColor);
    }

    @Override
    public void setTimezone(TimeZone timeZone) {
        super.setTimezone(timeZone);
        DATE_FORMAT.setTimeZone(CALENDAR.getTimeZone());
        DATE_NAMES_FORMAT.setTimeZone(CALENDAR.getTimeZone());
    }

    @Override
    public String getText() {
        CALENDAR.setTimeInMillis(System.currentTimeMillis());
        if (showDateNamesFormat) {
            return DATE_NAMES_FORMAT.format(CALENDAR.getTime());
        }
        return DATE_FORMAT.format(CALENDAR.getTime());
    }

    public void setShowDateNamesFormat(Boolean showDateNamesFormat) {
        this.showDateNamesFormat = showDateNamesFormat;
        getPaint().setTextSize(getPaint().getTextSize());
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
