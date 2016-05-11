package com.dimitrioskanellopoulos.athletica.matrix.columns;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateColumn extends Column {

    private static final Calendar calendar = Calendar.getInstance();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    @Override
    public String getText() {
        calendar.setTimeInMillis(System.currentTimeMillis());
        return dateFormat.format(calendar.getTime());
    }
}
