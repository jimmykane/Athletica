package com.dimitrioskanellopoulos.athletica.matrix.columns;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AmPmColumn extends Column {
    private static final Calendar calendar = Calendar.getInstance();
    private final static SimpleDateFormat timeFormat = new SimpleDateFormat("a");

    @Override
    public String getText() {
        calendar.setTimeInMillis(System.currentTimeMillis());
        return timeFormat.format(calendar.getTime());
    }
}
