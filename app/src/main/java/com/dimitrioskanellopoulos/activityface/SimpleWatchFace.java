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

    // Texts
    private final String sunriseSunsetTextTemplate;

    // Paints
    private final List<Paint> paints = new ArrayList<Paint>();
    private final Paint timePaint;
    private final Paint datePaint;
    private final Paint batteryLevelPaint;
    private final Paint sunriseSunsetPaint;
    private final Paint backgroundPaint;
    private final Time time;

    private boolean shouldShowSeconds = true;
    private String batteryLevelText = "";
    private String altitudeText = "";
    private String sunriseSunsetText = "";

    public SimpleWatchFace(Context context) {

        // Create fontAwesome typeface
        Typeface fontAwesome = Typeface.createFromAsset(context.getAssets(), "fonts/fontawesome-webfont.ttf");

        // Add paint for sunrise
        sunriseSunsetPaint = new Paint();
        sunriseSunsetPaint.setTypeface(fontAwesome);
        sunriseSunsetPaint.setColor(TEXT_DEFAULT_COLOUR);
        sunriseSunsetPaint.setTextSize(context.getResources().getDimension(R.dimen.text_size));
        sunriseSunsetPaint.setAntiAlias(true);
        paints.add(sunriseSunsetPaint);

        // Add paint for battery level
        Paint batteryLevelPaint = new Paint();
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

        this.timePaint = timePaint;
        this.datePaint = datePaint;
        this.batteryLevelPaint = batteryLevelPaint;
        this.backgroundPaint = backgroundPaint;

        // Get the texts
        sunriseSunsetTextTemplate = context.getResources().getString(R.string.sunrise_sunset_text);

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

        float sunriseSunsetTextXOffset = computeXOffset(sunriseSunsetText, sunriseSunsetPaint, bounds);
        float sunriseSunsetYOffset = computeTimeYOffset(sunriseSunsetText, sunriseSunsetPaint, bounds);
        canvas.drawText(sunriseSunsetText, sunriseSunsetTextXOffset, sunriseSunsetYOffset - dateYOffset, sunriseSunsetPaint);

        String bottomRowText = batteryLevelText + " " + altitudeText;
        float bottomRowTextXOffset = computeXOffset(bottomRowText, batteryLevelPaint, bounds);
        float bottomRowTextYOffset = computeTimeYOffset(bottomRowText, batteryLevelPaint, bounds);
        canvas.drawText(bottomRowText, bottomRowTextXOffset, bottomRowTextYOffset + sunriseSunsetYOffset - dateYOffset, batteryLevelPaint);
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

    public void updateBatteryLevel(String batteryLevel){
        batteryLevelText = batteryLevel + "%";
    }

    public void updatePressureAltitude(String altitude){
        altitudeText = "@" + altitude + "m";
    }

    public void updateSunriseSunset(Pair<String, String> sunriseSunset) {
        sunriseSunsetText =  String.format(sunriseSunsetTextTemplate, sunriseSunset.first, sunriseSunset.second);
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
