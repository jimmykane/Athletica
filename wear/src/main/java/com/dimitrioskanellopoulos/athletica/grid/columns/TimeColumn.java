package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class TimeColumn extends CalendarColumn {
    private final static String TAG = "SensorColumn";
    private final static SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("h:mm", Locale.getDefault());
    private final static SimpleDateFormat TIME_FORMAT_WITH_SECONDS = new SimpleDateFormat("h:mm:ss", Locale.getDefault());
    private final static SimpleDateFormat TIME_FORMAT_24 = new SimpleDateFormat("k:mm", Locale.getDefault());
    private final static SimpleDateFormat TIME_FORMAT_24_WITH_SECONDS = new SimpleDateFormat("k:mm:ss", Locale.getDefault());
    private final Float initTextSize;
    private Boolean isIn24hourFormat = true;

    public TimeColumn(Context context, Typeface paintTypeface, Float paintTextSize, int paintColor) {
        super(context, paintTypeface, paintTextSize, paintColor);
        initTextSize = paintTextSize;
        getPaint().setTextSize(initTextSize * 0.8f);
    }

    public void setTimeFormat24(Boolean timeFormat24) {
        this.isIn24hourFormat = timeFormat24;
    }

    @Override
    public void setAmbientMode(Boolean ambientMode) {
        super.setAmbientMode(ambientMode);
    }

    @Override
    public void setIsVisible(Boolean isVisible) {
        super.setIsVisible(isVisible);
        TIME_FORMAT.setTimeZone(CALENDAR.getTimeZone());
        TIME_FORMAT_WITH_SECONDS.setTimeZone(CALENDAR.getTimeZone());
        TIME_FORMAT_24.setTimeZone(CALENDAR.getTimeZone());
        TIME_FORMAT_24_WITH_SECONDS.setTimeZone(CALENDAR.getTimeZone());
    }

    @Override
    public String getText() {
        Float textSize = initTextSize;
        if (!isIn24hourFormat) {
            textSize = textSize * 0.90f;
        }
        if (!isInAmbientMode()) {
            textSize = textSize * 0.90f;
        }
        getPaint().setTextSize(textSize);

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
    public void destroy() {
        Log.d(TAG, "Destroyed");
    }
}
