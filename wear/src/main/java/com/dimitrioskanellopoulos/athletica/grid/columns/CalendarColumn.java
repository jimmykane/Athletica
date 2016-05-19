package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.graphics.Typeface;

import java.util.Calendar;
import java.util.TimeZone;

public abstract class CalendarColumn extends Column {
    protected static final Calendar calendar = Calendar.getInstance();

    public CalendarColumn(Typeface paintTypeface, Float paintTextSize, int paintColor) {
        super(paintTypeface, paintTextSize, paintColor);
    }

    public void setTimezone(TimeZone timeZone) {
        calendar.setTimeZone(timeZone);
    }
}
