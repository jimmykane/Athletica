package com.dimitrioskanellopoulos.athletica;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class WatchFace {
    private static final String TAG = "Watchface";
    private static final String TIME_FORMAT_WITHOUT_SECONDS = "%02d:%02d";
    private static final String TIME_FORMAT_WITH_SECONDS = TIME_FORMAT_WITHOUT_SECONDS + ":%02d";
    private static final String DATE_FORMAT = "%02d.%02d.%d";
    private static final int DATE_AND_TIME_DEFAULT_COLOUR = Color.WHITE;
    private static final int TEXT_DEFAULT_COLOUR = Color.WHITE;
    private static final int BACKGROUND_DEFAULT_COLOUR = Color.BLACK;

    // Paints. Keep it list for fast access or ordered
    private final List<TextRowPaint> paints = new ArrayList<TextRowPaint>();

    // Icons
    private final String sunIcon;
    private final String moonIcon;
    private final String areaChartIcon;
    private final String batteryEmptyIcon;
    private final String batteryQuarterIcon;
    private final String batteryHalfIcon;
    private final String batteryThreeQuartersIcon;
    private final String batteryFullIcon;

    private final Calendar calendar;

    private boolean shouldShowSeconds = true;
    private String batteryLevelText = "";
    private String altitudeText = "";

    public WatchFace(Context context) {

        // Create fontAwesome typeface
        Typeface fontAwesome = Typeface.createFromAsset(context.getAssets(), "fonts/fontawesome-webfont.ttf");

        // 0. Add paint for background
        TextRowPaint backgroundPaint = new TextRowPaint();
        backgroundPaint.setColor(BACKGROUND_DEFAULT_COLOUR);
        paints.add(backgroundPaint);
        // @todo Maybe should draw now ?

        // 1. Add paint for time
        TextRowPaint timePaint = new TextRowPaint();
        timePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        timePaint.setColor(DATE_AND_TIME_DEFAULT_COLOUR);
        timePaint.setTextSize(context.getResources().getDimension(R.dimen.time_size));
        timePaint.setAntiAlias(true);
        paints.add(timePaint);

        // 3. Add paint for date
        TextRowPaint datePaint = new TextRowPaint();
        datePaint.setColor(DATE_AND_TIME_DEFAULT_COLOUR);
        datePaint.setTextSize(context.getResources().getDimension(R.dimen.date_size));
        datePaint.setAntiAlias(true);
        datePaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
        paints.add(datePaint);

        // 2. Add paint for sunrise
        TextRowPaint sunriseSunsetPaint = new TextRowPaint();
        sunriseSunsetPaint.setTypeface(fontAwesome);
        sunriseSunsetPaint.setColor(TEXT_DEFAULT_COLOUR);
        sunriseSunsetPaint.setTextSize(context.getResources().getDimension(R.dimen.text_size));
        sunriseSunsetPaint.setAntiAlias(true);
        paints.add(sunriseSunsetPaint);

        // 4. Add paint for battery level
        TextRowPaint batteryLevelPaint = new TextRowPaint();
        batteryLevelPaint.setTypeface(fontAwesome);
        batteryLevelPaint.setColor(TEXT_DEFAULT_COLOUR);
        batteryLevelPaint.setTextSize(context.getResources().getDimension(R.dimen.text_size));
        batteryLevelPaint.setAntiAlias(true);
        paints.add(batteryLevelPaint);

        // Add the icons
        sunIcon = context.getResources().getString(R.string.sun_icon);
        moonIcon = context.getResources().getString(R.string.moon_icon);
        areaChartIcon = context.getResources().getString(R.string.area_chart_icon);
        batteryEmptyIcon = context.getResources().getString(R.string.battery_empty_icon);
        batteryQuarterIcon = context.getResources().getString(R.string.battery_quarter_icon);
        batteryHalfIcon = context.getResources().getString(R.string.battery_half_icon);
        batteryThreeQuartersIcon = context.getResources().getString(R.string.battery_three_quarters_icon);
        batteryFullIcon = context.getResources().getString(R.string.battery_full_icon);

        calendar = Calendar.getInstance();
    }

    public void draw(Canvas canvas, Rect bounds) {

        // Update time
        calendar.setTimeInMillis(System.currentTimeMillis());

        // First draw background
        canvas.drawRect(0, 0, bounds.width(), bounds.height(), paints.get(0));

        // Set texts on the most recent times for time
        paints.get(1).setText(String.format(
                shouldShowSeconds ?
                        TIME_FORMAT_WITH_SECONDS :
                        TIME_FORMAT_WITHOUT_SECONDS,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND)));

        // And date
        paints.get(2).setText(String.format(DATE_FORMAT, calendar.get(calendar.DAY_OF_MONTH), calendar.get(calendar.MONTH), calendar.get(calendar.YEAR)));
        paints.get(4).setText(batteryLevelText + "    " + altitudeText);

        // Draw the 1st one
        Float yOffset = 0f;
        for (Integer i = 1; i < paints.size(); i++) {
            yOffset = yOffset + computeRowYOffset(paints.get(i));
            if (i == 1) {
                yOffset = computeFirstPaintYOffset(paints.get(i), bounds);
            }
            Float xOffset = computeXOffset(paints.get(i), bounds);
            canvas.drawText(paints.get(i).getText(), xOffset, yOffset, paints.get(i));
        }
    }

    private float computeXOffset(TextRowPaint paint, Rect watchBounds) {
        float centerX = watchBounds.exactCenterX();
        float textLength = paint.measureText(paint.getText());
        return centerX - (textLength / 2.0f);
    }

    private float computeFirstPaintYOffset(TextRowPaint firstRowPaint, Rect watchBounds) {
        float centerY = watchBounds.exactCenterY() - 15.0f;
        Rect textBounds = new Rect();
        firstRowPaint.getTextBounds(firstRowPaint.getText(), 0, firstRowPaint.getText().length(), textBounds);
        int textHeight = textBounds.height();
        return centerY + (textHeight / 2.0f);
    }

    private float computeRowYOffset(TextRowPaint paint) {
        Rect textBounds = new Rect();
        paint.getTextBounds(paint.getText(), 0, paint.getText().length(), textBounds);
        return textBounds.height() + 15.0f;
    }

    public void setAntiAlias(boolean antiAlias) {
        for (Paint paint : paints) {
            paint.setAntiAlias(antiAlias);
        }
    }

    public void updateTimeZoneWith(TimeZone timeZone) {
        calendar.setTimeZone(timeZone);
    }

    public void setShowSeconds(boolean showSeconds) {
        shouldShowSeconds = showSeconds;
    }


    public void updateBatteryLevel(Integer batteryPercentage) {
        String icon;
        if (batteryPercentage > 80 && batteryPercentage <= 100) {
            icon = batteryFullIcon;
        } else if (batteryPercentage > 60 && batteryPercentage <= 80) {
            icon = batteryThreeQuartersIcon;
        } else if (batteryPercentage > 40 && batteryPercentage <= 60) {
            icon = batteryHalfIcon;
        } else if (batteryPercentage >= 20 && batteryPercentage <= 40) {
            icon = batteryQuarterIcon;
        } else {
            icon = batteryEmptyIcon;
        }
        batteryLevelText = icon + " " + batteryPercentage + "%";
    }

    public void updateAltitude(String altitude) {
        altitudeText = areaChartIcon + " " + altitude + "m";
    }

    public void updateSunriseSunset(Pair<String, String> sunriseSunset) {
        paints.get(3).setText(sunIcon + " " + sunriseSunset.first + "    " + moonIcon + " " + sunriseSunset.second);
    }

}
