package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import com.dimitrioskanellopoulos.athletica.grid.columns.abstracts.CalendarColumn;

import org.apache.commons.lang3.time.FastDateFormat;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

public class DateColumn extends CalendarColumn {
    private final static String TAG = "DateColumn";

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getInstance("dd.MM.yyyy", TimeZone.getDefault(),  Locale.getDefault());
    private static final FastDateFormat DATE_NAMES_FORMAT = FastDateFormat.getInstance("E d MMM yyyy",TimeZone.getDefault(),  Locale.getDefault());

    private FastDateFormat dateFormat = DATE_FORMAT;

    public DateColumn(Context context, Typeface paintTypeface, Float paintTextSize, int paintColor, Boolean visible, Boolean ambientMode) {
        super(context, paintTypeface, paintTextSize, paintColor, visible, ambientMode);
    }

    @Override
    public String getText() {
        CALENDAR.setTimeInMillis(System.currentTimeMillis());
        return dateFormat.format(CALENDAR);
    }

    public void setShowDateNamesFormat(Boolean showDateNamesFormat) {
        if (showDateNamesFormat){
            dateFormat = DATE_NAMES_FORMAT;
        }else {
            dateFormat = DATE_FORMAT;
        }
        getPaint().setTextSize(getPaint().getTextSize());
    }

    @Override
    public void destroy() {
        Log.d(TAG, "Destroyed");
    }
}
