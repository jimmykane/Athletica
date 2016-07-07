package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.content.Context;
import android.graphics.Typeface;

public class SunsetColumn extends SunriseSunsetColumn {
    private static final String TAG = "SunsetColumn";

    public SunsetColumn(Context context, Typeface paintTypeface, Float paintTextSize, int paintColor) {
        super(context, paintTypeface, paintTextSize, paintColor);
    }

    @Override
    public String getText() {
        if (sunriseSunset == null) {
            return super.getText();
        }
        return sunriseSunset.second;
    }
}
