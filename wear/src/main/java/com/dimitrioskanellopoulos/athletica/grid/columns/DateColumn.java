package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.graphics.Typeface;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateColumn extends CalendarColumn {

    private static final Calendar calendar = Calendar.getInstance();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
    private static final SimpleDateFormat dateNamesFormat = new SimpleDateFormat("E d LLL yyyy", Locale.getDefault());

    private Boolean showDateNamesFormat = false;

    public DateColumn(Typeface paintTypeface, Float paintTextSize, int paintColor) {
        super(paintTypeface, paintTextSize, paintColor);
    }

    @Override
    public void setTimezone(TimeZone timeZone) {
        super.setTimezone(timeZone);
        dateFormat.setTimeZone(calendar.getTimeZone());
        dateNamesFormat.setTimeZone(calendar.getTimeZone());
    }

    @Override
    public String getText() {
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (showDateNamesFormat) {
            return dateNamesFormat.format(calendar.getTime());
        }
        return dateFormat.format(calendar.getTime());
    }

    public void setShowDateNamesFormat(Boolean showDateNamesFormat) {
        this.showDateNamesFormat = showDateNamesFormat;
        getPaint().setTextSize(getPaint().getTextSize() * 1f);
    }
}
