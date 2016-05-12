package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.graphics.Typeface;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class TimeColumn extends CalendarColumn {
    private final static SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm", Locale.getDefault());
    private final static SimpleDateFormat timeFormatWithSeconds = new SimpleDateFormat("h:mm:ss", Locale.getDefault());
    private final static SimpleDateFormat timeFormat24 = new SimpleDateFormat("k:mm", Locale.getDefault());
    private final static SimpleDateFormat timeFormat24WithSeconds = new SimpleDateFormat("k:mm:ss", Locale.getDefault());
    private Boolean isIn24hourFormat = true;

    public TimeColumn(Typeface paintTypeface, Float paintTextSize, int paintColor) {
        super(paintTypeface, paintTextSize, paintColor);
    }

    public void setTimeFormat24(Boolean timeFormat24) {
        this.isIn24hourFormat = timeFormat24;
    }

    @Override
    public void setTimezone(TimeZone timeZone) {
        super.setTimezone(timeZone);
        timeFormat.setTimeZone(calendar.getTimeZone());
        timeFormatWithSeconds.setTimeZone(calendar.getTimeZone());
        timeFormat24.setTimeZone(calendar.getTimeZone());
        timeFormat24WithSeconds.setTimeZone(calendar.getTimeZone());
    }

    @Override
    public String getText() {
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (isInAmbientMode() && isIn24hourFormat) {
            return timeFormat24.format(calendar.getTime());
        } else if (isInAmbientMode() && !isIn24hourFormat) {
            return timeFormat.format(calendar.getTime());
        } else if (!isInAmbientMode() && isIn24hourFormat) {
            return timeFormat24WithSeconds.format(calendar.getTime());
        }
        return timeFormatWithSeconds.format(calendar.getTime());
    }
}
