package com.dimitrioskanellopoulos.activityface;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.format.Time;

public class SimpleWatchFace {

    private static final String TIME_FORMAT_WITHOUT_SECONDS = "%02d:%02d";
    private static final String TIME_FORMAT_WITH_SECONDS = TIME_FORMAT_WITHOUT_SECONDS + ":%02d";
    private static final String DATE_FORMAT = "%02d.%02d.%d";
    private static final int DATE_AND_TIME_DEFAULT_COLOUR = Color.WHITE;
    private static final int TEXT_DEFAULT_COLOUR = Color.WHITE;
    private static final int BACKGROUND_DEFAULT_COLOUR = Color.BLACK;

    private final Paint timePaint;
    private final Paint datePaint;
    private final Paint sunrisePaint;
    private final Paint sunsetPaint;
    private final Paint backgroundPaint;
    private final Time time;

    private boolean shouldShowSeconds = true;
    private String sunriseText = "";
    private String sunsetText = "";
    private int backgroundColour = BACKGROUND_DEFAULT_COLOUR;
    private int dateAndTimeColour = DATE_AND_TIME_DEFAULT_COLOUR;

    public static SimpleWatchFace newInstance(Context context) {

        Paint sunrisePaint = new Paint();
        sunrisePaint.setColor(TEXT_DEFAULT_COLOUR);
        sunrisePaint.setTextSize(context.getResources().getDimension(R.dimen.text_size));
        sunrisePaint.setAntiAlias(true);

        Paint timePaint = new Paint();
        timePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        timePaint.setColor(DATE_AND_TIME_DEFAULT_COLOUR);
        timePaint.setTextSize(context.getResources().getDimension(R.dimen.time_size));
        timePaint.setAntiAlias(true);

        Paint datePaint = new Paint();
        datePaint.setColor(DATE_AND_TIME_DEFAULT_COLOUR);
        datePaint.setTextSize(context.getResources().getDimension(R.dimen.date_size));
        datePaint.setAntiAlias(true);

        Paint sunsetPaint = new Paint();
        sunsetPaint.setColor(TEXT_DEFAULT_COLOUR);
        sunsetPaint.setTextSize(context.getResources().getDimension(R.dimen.text_size));
        sunsetPaint.setAntiAlias(true);

        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(BACKGROUND_DEFAULT_COLOUR);

        return new SimpleWatchFace(timePaint, datePaint, sunrisePaint, sunsetPaint ,backgroundPaint, new Time());
    }

    SimpleWatchFace(Paint timePaint, Paint datePaint, Paint sunrisePaint, Paint sunsetPaint, Paint backgroundPaint, Time time) {
        this.timePaint = timePaint;
        this.datePaint = datePaint;
        this.sunrisePaint = sunrisePaint;
        this.sunsetPaint = sunsetPaint;
        this.backgroundPaint = backgroundPaint;
        this.time = time;
    }

    public void draw(Canvas canvas, Rect bounds) {
        time.setToNow();
        canvas.drawRect(0, 0, bounds.width(), bounds.height(), backgroundPaint);

        String timeText = String.format(shouldShowSeconds ? TIME_FORMAT_WITH_SECONDS : TIME_FORMAT_WITHOUT_SECONDS, time.hour, time.minute, time.second);
        float timeXOffset = computeXOffset(timeText, timePaint, bounds);
        float timeYOffset = computeTimeYOffset(timeText, timePaint, bounds);
        canvas.drawText(timeText, timeXOffset, timeYOffset, timePaint);

        String dateText = String.format(DATE_FORMAT, time.monthDay, (time.month + 1), time.year);
        float dateXOffset = computeXOffset(dateText, datePaint, bounds);
        float dateYOffset = computeDateYOffset(dateText, datePaint);
        canvas.drawText(dateText, dateXOffset, timeYOffset + dateYOffset, datePaint);

        float sunriseTextXOffset = computeXOffset(sunsetText, sunsetPaint, bounds);
        float sunriseTextYOffset = computeTimeYOffset(sunsetText, sunsetPaint, bounds);
        canvas.drawText(sunriseText, sunriseTextXOffset, sunriseTextYOffset - dateYOffset, sunrisePaint);

        float sunsetTextXOffset = computeXOffset(sunsetText, sunsetPaint, bounds);
        float simpleTextYOffset = computeTimeYOffset(sunsetText, sunsetPaint, bounds);
        canvas.drawText(sunsetText, sunsetTextXOffset, simpleTextYOffset + 2*dateYOffset, sunsetPaint);
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
        timePaint.setAntiAlias(antiAlias);
        datePaint.setAntiAlias(antiAlias);
    }

    public void updateDateAndTimeColourTo(int colour) {
        dateAndTimeColour = colour;
        timePaint.setColor(colour);
        datePaint.setColor(colour);
    }

    public void updateTimeZoneWith(String timeZone) {
        time.clear(timeZone);
        time.setToNow();
    }

    public void setShowSeconds(boolean showSeconds) {
        shouldShowSeconds = showSeconds;
    }

    public void updateBackgroundColourTo(int colour) {
        backgroundColour = colour;
        backgroundPaint.setColor(colour);
    }

    public void updateSunrise(String time) {
        sunriseText =  "Sunrise at " + time;
    }

    public void updateSunset(String time) {
        sunsetText = "Sunset at " + time;
    }

    public void restoreBackgroundColour() {
        backgroundPaint.setColor(backgroundColour);
    }

    public void updateBackgroundColourToDefault() {
        backgroundPaint.setColor(BACKGROUND_DEFAULT_COLOUR);
    }

    public void updateDateAndTimeColourToDefault() {
        timePaint.setColor(DATE_AND_TIME_DEFAULT_COLOUR);
        datePaint.setColor(DATE_AND_TIME_DEFAULT_COLOUR);
    }

    public void restoreDateAndTimeColour() {
        timePaint.setColor(dateAndTimeColour);
        datePaint.setColor(dateAndTimeColour);
    }
}
