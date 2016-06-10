package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import com.dimitrioskanellopoulos.athletica.grid.columns.abstracts.CalendarColumn;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class DateColumn extends CalendarColumn {
    private final static String TAG = "DateColumn";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private static final SimpleDateFormat DATE_NAMES_FORMAT = new SimpleDateFormat("E d LLL yyyy", Locale.getDefault());

    private Boolean showDateNamesFormat = false;

    public DateColumn(Context context, Typeface paintTypeface, Float paintTextSize, int paintColor, Boolean visible, Boolean ambientMode ) {
        super(context, paintTypeface, paintTextSize, paintColor, visible, ambientMode);
    }

    @Override
    public void setIsVisible(Boolean isVisible) {
        super.setIsVisible(isVisible);
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
    public void destroy() {
        Log.d(TAG, "Destroyed");
    }
}
