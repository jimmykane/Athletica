package com.dimitrioskanellopoulos.athletica.matrix.columns;

import android.graphics.Typeface;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateColumn extends Column {

    private static final Calendar calendar = Calendar.getInstance();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    private static final SimpleDateFormat dateNamesFormat = new SimpleDateFormat("E d LLL yyyy");

    private Boolean showDateNamesFormat = false;

    public DateColumn(Typeface paintTypeface, Float paintTextSize, int paintColor) {
        super(paintTypeface, paintTextSize, paintColor);
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
