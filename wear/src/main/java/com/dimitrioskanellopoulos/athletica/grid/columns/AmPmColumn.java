package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import com.dimitrioskanellopoulos.athletica.grid.columns.abstracts.CalendarColumn;

import org.apache.commons.lang3.time.FastDateFormat;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class AmPmColumn extends CalendarColumn {
    private static final String TAG = "AmPmColumn";
    private final static FastDateFormat TIME_FORMAT = FastDateFormat.getInstance("a", TimeZone.getDefault(),Locale.getDefault());

    public AmPmColumn(Context context, Typeface paintTypeface, Float paintTextSize, int paintColor, Boolean visible, Boolean ambientMode) {
        super(context, paintTypeface, paintTextSize, paintColor, visible, ambientMode);
    }

    @Override
    public void setIsVisible(Boolean isVisible) {
        super.setIsVisible(isVisible);
        //TIME_FORMAT.setTimeZone(CALENDAR.getTimeZone());
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
