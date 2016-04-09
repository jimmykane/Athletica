package com.dimitrioskanellopoulos.activityface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.format.Time;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class SimpleWatchFace {

    private static final String TIME_FORMAT_WITHOUT_SECONDS = "%02d:%02d";
    private static final String TIME_FORMAT_WITH_SECONDS = TIME_FORMAT_WITHOUT_SECONDS + ":%02d";
    private static final String DATE_FORMAT = "%02d.%02d.%d";
    private static final int DATE_AND_TIME_DEFAULT_COLOUR = Color.WHITE;
    private static final int TEXT_DEFAULT_COLOUR = Color.WHITE;
    private static final int BACKGROUND_DEFAULT_COLOUR = Color.BLACK;

    // Paints
    private final List<Paint> paints = new ArrayList<Paint>();
    private final Paint timePaint;
    private final Paint datePaint;
    private final Paint batteryLevelPaint;
    private final Paint firstRowPaint;
    private final Paint backgroundPaint;

    // Icons
    private final String sunIcon;
    private final String moonIcon;
    private final String areaChartIcon;
    private final String batteryEmptyIcon;
    private final String batteryQuarterIcon;
    private final String batteryHalfIcon;
    private final String batteryThreeQuartersIcon;
    private final String batteryFullIcon;

    private final Time time;

    private boolean shouldShowSeconds = true;
    private String batteryLevelText = "";
    private String altitudeText = "";
    private String firstRowText = "";

    public SimpleWatchFace(Context context) {

        // Create fontAwesome typeface
        Typeface fontAwesome = Typeface.createFromAsset(context.getAssets(), "fonts/fontawesome-webfont.ttf");

        // Add paint for sunrise
        firstRowPaint = new Paint();
        firstRowPaint.setTypeface(fontAwesome);
        firstRowPaint.setColor(TEXT_DEFAULT_COLOUR);
        firstRowPaint.setTextSize(context.getResources().getDimension(R.dimen.text_size));
        firstRowPaint.setAntiAlias(true);
        paints.add(firstRowPaint);

        // Add paint for battery level
        Paint batteryLevelPaint = new Paint();
        batteryLevelPaint.setTypeface(fontAwesome);
        batteryLevelPaint.setColor(TEXT_DEFAULT_COLOUR);
        batteryLevelPaint.setTextSize(context.getResources().getDimension(R.dimen.text_size));
        batteryLevelPaint.setAntiAlias(true);
        paints.add(batteryLevelPaint);

        // Add paint for time
        Paint timePaint = new Paint();
        timePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        timePaint.setColor(DATE_AND_TIME_DEFAULT_COLOUR);
        timePaint.setTextSize(context.getResources().getDimension(R.dimen.time_size));
        timePaint.setAntiAlias(true);
        paints.add(timePaint);

        // Add paint for date
        Paint datePaint = new Paint();
        datePaint.setColor(DATE_AND_TIME_DEFAULT_COLOUR);
        datePaint.setTextSize(context.getResources().getDimension(R.dimen.date_size));
        datePaint.setAntiAlias(true);
        datePaint.setTypeface(Typeface.create(Typeface.MONOSPACE, Typeface.NORMAL));
        paints.add(datePaint);

        // Add paint for background
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(BACKGROUND_DEFAULT_COLOUR);
        paints.add(backgroundPaint);

        // Add the icons
        sunIcon = context.getResources().getString(R.string.sun_icon);
        moonIcon = context.getResources().getString(R.string.moon_icon);
        areaChartIcon = context.getResources().getString(R.string.area_chart_icon);
        batteryEmptyIcon = context.getResources().getString(R.string.battery_empty_icon);
        batteryQuarterIcon = context.getResources().getString(R.string.battery_quarter_icon);
        batteryHalfIcon = context.getResources().getString(R.string.battery_half_icon);
        batteryThreeQuartersIcon = context.getResources().getString(R.string.battery_three_quarters_icon);
        batteryFullIcon = context.getResources().getString(R.string.battery_full_icon);

        this.timePaint = timePaint;
        this.datePaint = datePaint;
        this.batteryLevelPaint = batteryLevelPaint;
        this.backgroundPaint = backgroundPaint;

        this.time = new Time();
    }

    public void draw(Canvas canvas, Rect bounds) {
        time.setToNow();
        // Should calc in foreach
        canvas.drawRect(0, 0, bounds.width(), bounds.height(), backgroundPaint);

        String timeText = String.format(shouldShowSeconds ? TIME_FORMAT_WITH_SECONDS : TIME_FORMAT_WITHOUT_SECONDS, time.hour, time.minute, time.second);
        float timeXOffset = computeXOffset(timeText, timePaint, bounds);
        float timeYOffset = computeTimeYOffset(timeText, timePaint, bounds);
        canvas.drawText(timeText, timeXOffset, timeYOffset, timePaint);

        String dateText = String.format(DATE_FORMAT, time.monthDay, (time.month + 1), time.year);
        float dateXOffset = computeXOffset(dateText, datePaint, bounds);
        float dateYOffset = computeDateYOffset(dateText, datePaint);
        canvas.drawText(dateText, dateXOffset, timeYOffset + dateYOffset, datePaint);

        float firstRowTextXOffset = computeXOffset(firstRowText, firstRowPaint, bounds);
        float firstRowYOffset = computeTimeYOffset(firstRowText, firstRowPaint, bounds);
        canvas.drawText(firstRowText, firstRowTextXOffset, firstRowYOffset - dateYOffset, firstRowPaint);

        String bottomRowText = batteryLevelText + "    " +  altitudeText;
        float bottomRowTextXOffset = computeXOffset(bottomRowText, batteryLevelPaint, bounds);
        float bottomRowTextYOffset = computeTimeYOffset(bottomRowText, batteryLevelPaint, bounds);
        canvas.drawText(bottomRowText, bottomRowTextXOffset, bottomRowTextYOffset*2 - dateYOffset, batteryLevelPaint);
    }

    private float computeXOffset(String text, Paint paint, Rect watchBounds) {
        float centerX = watchBounds.exactCenterX();
        float textLength = paint.measureText(text);
        return centerX - (textLength / 2.0f);
    }

    private float computeTimeYOffset(String timeText, Paint timePaint, Rect watchBounds) {
        float centerY = watchBounds.exactCenterY() - 17.0f;
        Rect textBounds = new Rect();
        timePaint.getTextBounds(timeText, 0, timeText.length(), textBounds);
        int textHeight = textBounds.height();
        return centerY + (textHeight / 2.0f);
    }

    private float computeDateYOffset(String dateText, Paint datePaint) {
        Rect textBounds = new Rect();
        datePaint.getTextBounds(dateText, 0, dateText.length(), textBounds);
        return textBounds.height() + 17.0f;
    }

    public void setAntiAlias(boolean antiAlias) {
        for (Paint paint: this.paints) {
            paint.setAntiAlias(antiAlias);
        }
    }

    public void updateTimeZoneWith(String timeZone) {
        time.clear(timeZone);
        time.setToNow();
    }

    public void setShowSeconds(boolean showSeconds) {
        shouldShowSeconds = showSeconds;
    }

    public void updateBatteryLevel(Float batteryPercentage){
        String icon;
        batteryLevelText = batteryFullIcon + " " + String.format("%.0f", batteryPercentage*100) + "%";
    }

    public void updatePressureAltitude(String altitude){
        altitudeText = areaChartIcon + " " + altitude + "m";
    }

    public void updateSunriseSunset(Pair<String, String> sunriseSunset) {
        firstRowText = sunIcon + " " + sunriseSunset.first + "    " +moonIcon + " " + sunriseSunset.second;
    }

    public void restoreBackgroundColour() {
        int backgroundColour = BACKGROUND_DEFAULT_COLOUR;
        this.backgroundPaint.setColor(backgroundColour);
    }

    public void updateBackgroundColourToDefault() {
        backgroundPaint.setColor(BACKGROUND_DEFAULT_COLOUR);
    }

    public void updateDateAndTimeColourToDefault() {
        timePaint.setColor(DATE_AND_TIME_DEFAULT_COLOUR);
        datePaint.setColor(DATE_AND_TIME_DEFAULT_COLOUR);
    }

    public void restoreDateAndTimeColour() {
        int dateAndTimeColour = DATE_AND_TIME_DEFAULT_COLOUR;
        timePaint.setColor(dateAndTimeColour);
        datePaint.setColor(dateAndTimeColour);
    }
}
