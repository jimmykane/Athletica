package com.dimitrioskanellopoulos.athletica.matrix.columns;

import android.graphics.Typeface;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class AmPmColumn extends CalendarColumn {
    private static final Calendar calendar = Calendar.getInstance();
    private final static SimpleDateFormat timeFormat = new SimpleDateFormat("a", Locale.getDefault());

    public AmPmColumn(Typeface paintTypeface, Float paintTextSize, int paintColor) {
        super(paintTypeface, paintTextSize, paintColor);
    }

    @Override
    public void setTimezone(TimeZone timeZone) {
        super.setTimezone(timeZone);
        timeFormat.setTimeZone(calendar.getTimeZone());
    }

    @Override
    public String getText() {
        calendar.setTimeInMillis(System.currentTimeMillis());
        return timeFormat.format(calendar.getTime());
    }
}
