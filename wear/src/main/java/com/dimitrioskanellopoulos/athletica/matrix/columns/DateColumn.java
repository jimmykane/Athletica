package com.dimitrioskanellopoulos.athletica.matrix.columns;

import android.graphics.Typeface;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateColumn extends Column {

    private static final Calendar calendar = Calendar.getInstance();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public DateColumn(Typeface paintTypeface, Float paintTextSize, int paintColor) {
        super(paintTypeface, paintTextSize, paintColor);
    }

    @Override
    public String getText() {
        calendar.setTimeInMillis(System.currentTimeMillis());
        return dateFormat.format(calendar.getTime());
    }
}
