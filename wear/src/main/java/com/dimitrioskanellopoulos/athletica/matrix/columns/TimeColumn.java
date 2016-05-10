package com.dimitrioskanellopoulos.athletica.matrix.columns;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimeColumn extends Column {
    private final static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
    private final static SimpleDateFormat timeFormatWithSeconds = new SimpleDateFormat("hh:mm:ss");
    private final static SimpleDateFormat timeFormat24 = new SimpleDateFormat("k:mm");
    private final static SimpleDateFormat timeFormat24WithSeconds = new SimpleDateFormat("k:mm:ss");

    private Boolean isIn24hourFormat = true;

    private static final Calendar calendar = Calendar.getInstance();

    public void setTimeFormat24(Boolean timeFormat24) {
        this.isIn24hourFormat = timeFormat24;
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
