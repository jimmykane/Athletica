package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.graphics.Typeface;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class TimeColumn extends CalendarColumn {
    private final static String TAG = "SensorColumn";
    private final static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("h:mm", Locale.getDefault());
    private final static SimpleDateFormat TIME_FORMAT_WITH_SECONDS = new SimpleDateFormat("h:mm:ss", Locale.getDefault());
    private final static SimpleDateFormat TIME_FORMAT_24 = new SimpleDateFormat("k:mm", Locale.getDefault());
    private final static SimpleDateFormat TIME_FORMAT_24_WITH_SECONDS = new SimpleDateFormat("k:mm:ss", Locale.getDefault());
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
        TIME_FORMAT.setTimeZone(CALENDAR.getTimeZone());
        TIME_FORMAT_WITH_SECONDS.setTimeZone(CALENDAR.getTimeZone());
        TIME_FORMAT_24.setTimeZone(CALENDAR.getTimeZone());
        TIME_FORMAT_24_WITH_SECONDS.setTimeZone(CALENDAR.getTimeZone());
    }

    @Override
    public String getText() {
        CALENDAR.setTimeInMillis(System.currentTimeMillis());
        if (isInAmbientMode() && isIn24hourFormat) {
            return TIME_FORMAT_24.format(CALENDAR.getTime());
        } else if (isInAmbientMode() && !isIn24hourFormat) {
            return TIME_FORMAT.format(CALENDAR.getTime());
        } else if (!isInAmbientMode() && isIn24hourFormat) {
            return TIME_FORMAT_24_WITH_SECONDS.format(CALENDAR.getTime());
        }
        return TIME_FORMAT_WITH_SECONDS.format(CALENDAR.getTime());
    }

    @Override
    public void start() {
        Log.d(TAG, "started");
    }

    @Override
    public void destroy() {
        Log.d(TAG, "destroyed");
    }
}
