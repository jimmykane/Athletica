package com.dimitrioskanellopoulos.athletica.paints;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class TimePaint extends TextPaint {
    private final static SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
    private final static SimpleDateFormat timeFormatWithSeconds = new SimpleDateFormat("hh:mm:ss");
    private final static SimpleDateFormat timeFormat24 = new SimpleDateFormat("kk:mm");
    private final static SimpleDateFormat timeFormat24WithSeconds = new SimpleDateFormat("kk:mm:ss");

    private Boolean isIn24hourFormat = true;
    private SimpleDateFormat dateFormat;


    private static final Calendar calendar = Calendar.getInstance();

    public void setTimeFormat24 (Boolean timeFormat24){
        this.isIn24hourFormat = timeFormat24;
    }

    @Override
    public String getText() {
        if (isInAmbientMode() && isIn24hourFormat){
            return timeFormat24.format(calendar.getTime());
        }else if(isInAmbientMode() && !isIn24hourFormat){
            return timeFormat.format(calendar.getTime());
        }else if (!isInAmbientMode() && isIn24hourFormat){
            return timeFormat24WithSeconds.format(calendar.getTime());
        }else //(!isInAmbientMode() && !isIn24hourFormat)
        {
            return timeFormatWithSeconds.format(calendar.getTime());
        }
    }

}