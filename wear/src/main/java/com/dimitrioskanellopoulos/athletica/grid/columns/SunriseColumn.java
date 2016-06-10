package com.dimitrioskanellopoulos.athletica.grid.columns;

import android.content.Context;
import android.graphics.Typeface;

public class SunriseColumn extends SunriseSunsetColumn {
    private static final String TAG = "SunriseColumn";

    public SunriseColumn(Context context, Typeface paintTypeface, Float paintTextSize, int paintColor, Boolean visible, Boolean ambientMode) {
        super(context, paintTypeface, paintTextSize, paintColor,visible,ambientMode);
    }

    @Override
    public String getText() {
        if (sunriseSunset == null) {
            return super.getText();
        }
        return sunriseSunset.first;
    }
}
