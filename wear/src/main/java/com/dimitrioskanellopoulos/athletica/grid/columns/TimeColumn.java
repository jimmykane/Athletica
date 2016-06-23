package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;

import com.dimitrioskanellopoulos.athletica.grid.columns.abstracts.CalendarColumn;

import org.apache.commons.lang3.time.FastDateFormat;

import java.util.Locale;

public class TimeColumn extends CalendarColumn {
    private final static String TAG = "SensorColumn";
    private final static FastDateFormat TIME_FORMAT = FastDateFormat.getInstance("h:mm", Locale.getDefault());
    private final static FastDateFormat TIME_FORMAT_WITH_SECONDS = FastDateFormat.getInstance("h:mm:ss", Locale.getDefault());
    private final static FastDateFormat TIME_FORMAT_24 = FastDateFormat.getInstance("k:mm", Locale.getDefault());
    private final static FastDateFormat TIME_FORMAT_24_WITH_SECONDS = FastDateFormat.getInstance("k:mm:ss", Locale.getDefault());
    private final Float initTextSize;
    private Boolean isIn24hourFormat = true;

    private FastDateFormat timeFormat = TIME_FORMAT_24;

    public TimeColumn(Context context, Typeface paintTypeface, Float paintTextSize, int paintColor) {
        super(context, paintTypeface, paintTextSize, paintColor);
        initTextSize = paintTextSize;
    }

    public void setTimeFormat24(Boolean timeFormat24) {
        this.isIn24hourFormat = timeFormat24;
        getPaint().setTextSize(timeFormat24 ? initTextSize : initTextSize * 0.9f);
        setTimeFormat();
    }

    private void setTimeFormat(){
        if (isInAmbientMode() && isIn24hourFormat) {
            timeFormat = TIME_FORMAT_24;
        } else if (isInAmbientMode() && !isIn24hourFormat) {
            timeFormat = TIME_FORMAT;
        } else if (!isInAmbientMode() && isIn24hourFormat) {
            timeFormat =  TIME_FORMAT_24_WITH_SECONDS;
        }else {
            timeFormat = TIME_FORMAT_WITH_SECONDS;
        }
    }

    @Override
    public void setAmbientMode(Boolean ambientMode) {
        super.setAmbientMode(ambientMode);
        getPaint().setTextSize(ambientMode ? getPaint().getTextSize() / 0.9f : getPaint().getTextSize() * 0.9f);
        setTimeFormat();
    }

    @Override
    public String getText() {
        CALENDAR.setTimeInMillis(System.currentTimeMillis());
        return timeFormat.format(CALENDAR);
    }


    @Override
    public void destroy() {
        Log.d(TAG, "Destroyed");
    }
}
