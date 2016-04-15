package com.dimitrioskanellopoulos.athletica;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Pair;
import android.util.TypedValue;

import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

public class WatchFace {
    private static final String TAG = "Watchface";
    private static final String TIME_FORMAT_WITHOUT_SECONDS = "%02d:%02d";
    private static final String TIME_FORMAT_WITH_SECONDS = TIME_FORMAT_WITHOUT_SECONDS + ":%02d";
    private static final String DATE_FORMAT = "%02d.%02d.%d";
    private static final int DATE_AND_TIME_DEFAULT_COLOUR = Color.WHITE;
    private static final int TEXT_DEFAULT_COLOUR = Color.WHITE;
    private static final int BACKGROUND_DEFAULT_COLOUR = Color.BLACK;

    // Standard Paints -> Time and Battery
    private final LinkedHashMap<String, TextRowPaint> standardPaints = new LinkedHashMap<String, TextRowPaint>();

    // Extra Paints -> Dynamic
    private final LinkedHashMap<String, TextRowPaint> extraPaints = new LinkedHashMap<String, TextRowPaint>();

    // Icons
    private final String sunIcon;
    private final String moonIcon;
    private final String areaChartIcon;
    private final String batteryEmptyIcon;
    private final String batteryQuarterIcon;
    private final String batteryHalfIcon;
    private final String batteryThreeQuartersIcon;
    private final String batteryFullIcon;

    private final Float rowVerticalSize;

    private final Calendar calendar;

    private boolean shouldShowSeconds = true;

    public WatchFace(Context context) {

        // Create fontAwesome typeface
        Typeface fontAwesome = Typeface.createFromAsset(context.getAssets(), "fonts/fontawesome-webfont.ttf");

        // Define the size of the rows for vertical
        rowVerticalSize = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                context.getResources().getDimension(R.dimen.row_vertical_size),
                context.getResources().getDisplayMetrics());

        // Add paint for background
        TextRowPaint backgroundPaint = new TextRowPaint();
        backgroundPaint.setColor(BACKGROUND_DEFAULT_COLOUR);
        standardPaints.put("backgroundPaint", backgroundPaint);
        // @todo Maybe should draw now ?

        // Add paint for time
        TextRowPaint timePaint = new TextRowPaint();
        timePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        timePaint.setColor(DATE_AND_TIME_DEFAULT_COLOUR);
        timePaint.setTextSize(context.getResources().getDimension(R.dimen.time_size));
        timePaint.setAntiAlias(true);
        standardPaints.put("timePaint", timePaint);

        // Add paint for battery level
        TextRowPaint batteryLevelPaint = new TextRowPaint();
        batteryLevelPaint.setTypeface(fontAwesome);
        batteryLevelPaint.setColor(TEXT_DEFAULT_COLOUR);
        batteryLevelPaint.setTextSize(context.getResources().getDimension(R.dimen.battery_text_size));
        batteryLevelPaint.setAntiAlias(true);
        standardPaints.put("batteryLevelPaint", batteryLevelPaint);

        // Add paint for date
        TextRowPaint datePaint = new TextRowPaint();
        datePaint.setColor(DATE_AND_TIME_DEFAULT_COLOUR);
        datePaint.setTextSize(context.getResources().getDimension(R.dimen.date_size));
        datePaint.setAntiAlias(true);
        datePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        extraPaints.put("datePaint", datePaint);

        // Add paint for sunrise
        TextRowPaint sunriseSunsetPaint = new TextRowPaint();
        sunriseSunsetPaint.setTypeface(fontAwesome);
        sunriseSunsetPaint.setColor(TEXT_DEFAULT_COLOUR);
        sunriseSunsetPaint.setTextSize(context.getResources().getDimension(R.dimen.text_size));
        sunriseSunsetPaint.setAntiAlias(true);
        extraPaints.put("sunriseSunsetPaint", sunriseSunsetPaint);

        // Add paint for altitude
        TextRowPaint altitudePaint = new TextRowPaint();
        altitudePaint.setTypeface(fontAwesome);
        altitudePaint.setColor(TEXT_DEFAULT_COLOUR);
        altitudePaint.setTextSize(context.getResources().getDimension(R.dimen.text_size));
        altitudePaint.setAntiAlias(true);
        extraPaints.put("altitudePaint", altitudePaint);

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
        canvas.drawRect(0, 0, bounds.width(), bounds.height(), standardPaints.get("backgroundPaint"));

        // Draw Time
        TextRowPaint timePaint = standardPaints.get("timePaint");
        timePaint.setText(String.format(
                shouldShowSeconds ?
                        TIME_FORMAT_WITH_SECONDS :
                        TIME_FORMAT_WITHOUT_SECONDS,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND)));
        canvas.drawText(timePaint.getText(), computeXOffset(timePaint, bounds), computeFirstRowYOffset(timePaint, bounds), timePaint);

        // Draw battery
        TextRowPaint batteryPaint = standardPaints.get("batteryLevelPaint");
        canvas.drawText(batteryPaint.getText(), computeXOffset(batteryPaint, bounds), computeLastRowYOffset(batteryPaint, bounds), batteryPaint);


        // Set the text of the data
        extraPaints.get("datePaint").setText(String.format(DATE_FORMAT, calendar.get(calendar.DAY_OF_MONTH), calendar.get(calendar.MONTH), calendar.get(calendar.YEAR)));

        // Draw the standard one since they are  fixed

        Float yOffset = computeFirstRowYOffset(timePaint, bounds);
        for (Map.Entry<String, TextRowPaint> entry : extraPaints.entrySet()) {
            TextRowPaint paint = entry.getValue();
            yOffset = yOffset + computeRowYOffset(paint);
            Float xOffset = computeXOffset(paint, bounds);
            canvas.drawText(paint.getText(), xOffset, yOffset, paint);
        }
    }

    private float computeXOffset(TextRowPaint paint, Rect watchBounds) {
        return  watchBounds.exactCenterX() - (paint.measureText(paint.getText()) / 2.0f);
    }

    private float computeFirstRowYOffset(TextRowPaint firstRowPaint, Rect watchBounds) {
        float centerY = watchBounds.exactCenterY() - 15.0f;
        Rect textBounds = new Rect();
        firstRowPaint.getTextBounds(firstRowPaint.getText(), 0, firstRowPaint.getText().length(), textBounds);
        int textHeight = textBounds.height();
        return centerY + (textHeight / 2.0f);
    }

    private float computeLastRowYOffset(TextRowPaint lastRowPaint, Rect watchBounds) {
        Rect textBounds = new Rect();
        lastRowPaint.getTextBounds(lastRowPaint.getText(), 0, lastRowPaint.getText().length(), textBounds);
        int textHeight = textBounds.height();
        return watchBounds.bottom - textHeight - 17.0f;
    }

    private float computeRowYOffset(TextRowPaint paint) {
        Rect textBounds = new Rect();
        paint.getTextBounds(paint.getText(), 0, paint.getText().length(), textBounds);
        return textBounds.height() + rowVerticalSize;
    }

    public void setAntiAlias(boolean antiAlias) {
        for (Map.Entry<String, TextRowPaint> entry : standardPaints.entrySet()) {
            entry.getValue().setAntiAlias(antiAlias);
        }
    }

    public void updateTimeZoneWith(TimeZone timeZone) {
        calendar.setTimeZone(timeZone);
    }

    public void setShowSeconds(boolean showSeconds) {
        shouldShowSeconds = showSeconds;
    }

    public void updateAltitude(String altitude) {
        extraPaints.get("altitudePaint").setText(areaChartIcon + " " + altitude + "m");
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
        standardPaints.get("batteryLevelPaint").setText(icon + " " + batteryPercentage + "%");
    }

    public void updateSunriseSunset(Pair<String, String> sunriseSunset) {
        extraPaints.get("sunriseSunsetPaint").setText(sunIcon + " " + sunriseSunset.first + "    " + moonIcon + " " + sunriseSunset.second);
    }

}
