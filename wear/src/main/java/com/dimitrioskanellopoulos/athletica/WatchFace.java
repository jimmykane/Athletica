package com.dimitrioskanellopoulos.athletica;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;

import com.dimitrioskanellopoulos.athletica.paints.SensorPaint;
import com.dimitrioskanellopoulos.athletica.paints.SunsetTimePaint;
import com.dimitrioskanellopoulos.athletica.paints.TextPaint;
import com.dimitrioskanellopoulos.athletica.paints.BatterySensorPaint;
import com.dimitrioskanellopoulos.athletica.paints.SensorPaintFactory;
import com.dimitrioskanellopoulos.athletica.paints.SunriseTimePaint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

public class WatchFace {
    private static final String TAG = "Watchface";

    /**
     * Standard String formats for time, date and color
     */
    private static final String TIME_FORMAT_WITHOUT_SECONDS = "%02d:%02d";
    private static final String TIME_FORMAT_WITH_SECONDS = TIME_FORMAT_WITHOUT_SECONDS + ":%02d";
    private static final String DATE_FORMAT = "%02d.%02d.%d";
    private static final int DATE_AND_TIME_DEFAULT_COLOUR = Color.WHITE;
    private static final int TEXT_DEFAULT_COLOUR = Color.WHITE;
    private static final int BACKGROUND_DEFAULT_COLOUR = Color.BLACK;

    private final Resources resources;

    private final Typeface fontAwesome;

    // The Calendar
    private static final Calendar calendar = Calendar.getInstance();

    // Background Paint
    private final android.graphics.Paint backgroundPaint;

    // First row of paints
    private final static LinkedHashMap<String, TextPaint> firstRowPaints = new LinkedHashMap<>();

    // Second row
    private final static LinkedHashMap<String, TextPaint> secondRowPaints = new LinkedHashMap<>();

    // Third row
    private final static LinkedHashMap<String, SensorPaint> thirdRowPaints = new LinkedHashMap<>();

    // Forth row
    protected final static LinkedHashMap<Integer, SensorPaint> forthRowPaints = new LinkedHashMap<>();

    // Last row
    protected final static LinkedHashMap<String, SensorPaint> lastRowPaints = new LinkedHashMap<>();

    // All the rows together
    private final static LinkedHashMap[] rows = {firstRowPaints, secondRowPaints, thirdRowPaints, forthRowPaints, lastRowPaints};

    private final Float rowVerticalMargin;

    private boolean shouldShowSeconds = true;

    private boolean isRound;
    private int chinSize;

    /**
     * The WatchFace. Everything the user sees. No extra init or data manipulation
     */
    public WatchFace(Context context) {

        resources = context.getResources();

        // Create fontAwesome typeface
        fontAwesome = Typeface.createFromAsset(context.getAssets(), "fonts/fontawesome-webfont.ttf");

        // Define the size of the rows for vertical
        rowVerticalMargin = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                resources.getDimension(R.dimen.row_vertical_margin),
                resources.getDisplayMetrics());

        // Add paint for background
        backgroundPaint = new Paint();
        backgroundPaint.setColor(BACKGROUND_DEFAULT_COLOUR);

        // Add paint for time
        TextPaint timePaint = new TextPaint();
        timePaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
        timePaint.setColor(DATE_AND_TIME_DEFAULT_COLOUR);
        timePaint.setTextSize(resources.getDimension(R.dimen.time_size));
        timePaint.setAntiAlias(true);
        firstRowPaints.put("timePaint", timePaint);

        // Add paint for battery level
        BatterySensorPaint batterySensorPaint = new BatterySensorPaint();
        batterySensorPaint.setTypeface(fontAwesome);
        batterySensorPaint.setColor(TEXT_DEFAULT_COLOUR);
        batterySensorPaint.setTextSize(resources.getDimension(R.dimen.battery_text_size));
        batterySensorPaint.setAntiAlias(true);
        batterySensorPaint.setText("0");
        lastRowPaints.put("batterySensorPaint", batterySensorPaint);

        // Add paint for date
        TextPaint datePaint = new TextPaint();
        datePaint.setColor(DATE_AND_TIME_DEFAULT_COLOUR);
        datePaint.setTextSize(resources.getDimension(R.dimen.date_size));
        datePaint.setAntiAlias(true);
        datePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        secondRowPaints.put("datePaint", datePaint);

        // Add paint for sunrise
        SensorPaint sunriseTimePaint = new SunriseTimePaint();
        sunriseTimePaint.setTypeface(fontAwesome);
        sunriseTimePaint.setColor(TEXT_DEFAULT_COLOUR);
        sunriseTimePaint.setTextSize(resources.getDimension(R.dimen.text_size));
        sunriseTimePaint.setAntiAlias(true);
        thirdRowPaints.put("sunriseTimePaint", sunriseTimePaint);

        // Add paint for sunset
        SensorPaint sunsetTimePaint = new SunsetTimePaint();
        sunsetTimePaint.setTypeface(fontAwesome);
        sunsetTimePaint.setColor(TEXT_DEFAULT_COLOUR);
        sunsetTimePaint.setTextSize(resources.getDimension(R.dimen.text_size));
        sunsetTimePaint.setAntiAlias(true);
        thirdRowPaints.put("sunsetTimePaint", sunsetTimePaint);
    }

    /**
     * @todo maybe cache calculations
     */
    public void draw(Canvas canvas, Rect bounds) {

        // Update time
        calendar.setTimeInMillis(System.currentTimeMillis());

        // First draw background
        canvas.drawRect(0, 0, bounds.width(), bounds.height(), backgroundPaint);

        // @todo refactor the shit out of this when you find your layout
        // Draw Time for now
        TextPaint timePaint = firstRowPaints.get("timePaint");
        timePaint.setText(String.format(
                shouldShowSeconds ?
                        TIME_FORMAT_WITH_SECONDS :
                        TIME_FORMAT_WITHOUT_SECONDS,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND)));
        Float yOffset = computeFirstRowYOffset(timePaint.getText(), timePaint, bounds);
        canvas.drawText(timePaint.getText(), computeXOffset(timePaint.getText(), timePaint, bounds), yOffset, timePaint);

        // Set the text of the data
        TextPaint datePaint = secondRowPaints.get("datePaint");
        datePaint.setText(String.format(DATE_FORMAT, calendar.get(calendar.DAY_OF_MONTH), calendar.get(calendar.MONTH) + 1, calendar.get(calendar.YEAR)));
        yOffset = yOffset + rowVerticalMargin + computeRowYOffset(datePaint.getText(), datePaint);
        canvas.drawText(datePaint.getText(), computeXOffset(datePaint.getText(), datePaint, bounds), yOffset, datePaint);

        // Until this gets combine look statically.
        SensorPaint sunrisePaint = thirdRowPaints.get("sunriseTimePaint"); // get the 1st
        SensorPaint sunsetPaint = thirdRowPaints.get("sunsetTimePaint"); // get the 2nd
        yOffset = yOffset + rowVerticalMargin + (computeRowYOffset(sunrisePaint.getText() + "    " + sunsetPaint.getText(), sunrisePaint));
        canvas.drawText(sunrisePaint.getText() + "   " + sunsetPaint.getText(), computeXOffset(sunrisePaint.getText() + "   " + sunsetPaint.getText(), sunrisePaint, bounds), yOffset, sunrisePaint);

        // Go over the sensor paints
        for (Map.Entry<Integer, SensorPaint> entry : forthRowPaints.entrySet()) {
            TextPaint paint = entry.getValue();
            if (paint.getText() == null) {
                continue;
            }
            yOffset = yOffset + rowVerticalMargin + computeRowYOffset(paint.getText(), paint);
            Float xOffset = computeXOffset(paint.getText(), paint, bounds);
            canvas.drawText(paint.getText(), xOffset, yOffset, paint);
        }

        // Draw battery
        SensorPaint batterySensorPaint = lastRowPaints.get("batterySensorPaint");
        float xOffsetTotal = computeXOffset(batterySensorPaint.getIcon() + batterySensorPaint.getText(), batterySensorPaint, bounds);

        canvas.drawText(batterySensorPaint.getIcon(), xOffsetTotal, computeLastRowYOffset(batterySensorPaint, bounds), batterySensorPaint);
        canvas.drawText(batterySensorPaint.getText(), xOffsetTotal + batterySensorPaint.measureText(batterySensorPaint.getIcon()) / 1f, computeLastRowYOffset(batterySensorPaint, bounds), batterySensorPaint);
    }

    /**
     * Computes the X-Axis offset so that the text is horizontally centered
     * @todo should go over array and called compute rowXOffset for paints
     */
    private float computeXOffset(String text, TextPaint paint, Rect watchBounds) {
        return watchBounds.exactCenterX() - (paint.measureText(text) / 2.0f);
    }

    /**
     * Computes the Y-Axis offset for the first row based on the exact center of the screen
     */
    private float computeFirstRowYOffset(String text, TextPaint firstRowPaint, Rect watchBounds) {
        float centerY = watchBounds.exactCenterY();
        Rect textBounds = new Rect();
        firstRowPaint.getTextBounds(text, 0, text.length(), textBounds);
        int textHeight = textBounds.height();
        return centerY + (textHeight / 2.0f);
    }

    /**
     * Computes the Y-Axis offset for a paint, according to it's size and margin
     */
    private float computeRowYOffset(String Text, TextPaint paint) {
        Rect textBounds = new Rect();
        paint.getTextBounds(Text, 0, Text.length(), textBounds);
        return (textBounds.height() / 2.0f);
    }

    /**
     * Computes the Y-Axis offset for the last row based on the bottom of the screen
     */
    private float computeLastRowYOffset(TextPaint lastRowPaint, Rect watchBounds) {
        Rect textBounds = new Rect();
        lastRowPaint.getTextBounds(lastRowPaint.getText(), 0, lastRowPaint.getText().length(), textBounds);
        return watchBounds.bottom - chinSize - (textBounds.height() / 2.0f);
    }

    /**
     * Toggles the ambient or not mode for all the paints
     */
    public void inAmbientMode(boolean inAmbientMode) {
        for (Map.Entry<String, TextPaint> entry : firstRowPaints.entrySet()) {
            entry.getValue().inAmbientMode(inAmbientMode);
        }
        for (Map.Entry<String, TextPaint> entry : secondRowPaints.entrySet()) {
            entry.getValue().inAmbientMode(inAmbientMode);
        }
        for (Map.Entry<String, SensorPaint> entry : thirdRowPaints.entrySet()) {
            entry.getValue().inAmbientMode(inAmbientMode);
        }
        for (Map.Entry<Integer, SensorPaint> entry : forthRowPaints.entrySet()) {
            entry.getValue().inAmbientMode(inAmbientMode);
        }
        for (Map.Entry<String, SensorPaint> entry : lastRowPaints.entrySet()) {
            entry.getValue().inAmbientMode(inAmbientMode);
        }
    }

    public void updateTimeZoneWith(TimeZone timeZone) {
        calendar.setTimeZone(timeZone);
    }

    public void setShowSeconds(boolean showSeconds) {
        shouldShowSeconds = showSeconds;
    }

    public void setIsRound(boolean round) {
        isRound = round;
    }

    public void setChinSize(Integer chinSize) {
        this.chinSize = chinSize;
    }

    public void addSensorPaint(Integer sensorType) {
        Log.d(TAG, "Added paint for " + sensorType);
        SensorPaint sensorPaint = SensorPaintFactory.getPaintForSensorType(sensorType);
        sensorPaint.setTypeface(fontAwesome);
        sensorPaint.setColor(TEXT_DEFAULT_COLOUR);
        sensorPaint.setTextSize(resources.getDimension(R.dimen.text_size));
        sensorPaint.setAntiAlias(true);
        forthRowPaints.put(sensorType, sensorPaint);
    }

    public void removeSensorPaint(Integer key) {
        forthRowPaints.remove(key);
    }

    public void updateSensorPaintText(Integer key, String value) {
        forthRowPaints.get(key).setText(value);
    }

    public void updateBatteryLevel(Integer batteryPercentage) {
        lastRowPaints.get("batterySensorPaint").setText(batteryPercentage.toString());
    }

    public void updateSunriseSunset(Pair<String, String> sunriseSunset) {
        thirdRowPaints.get("sunriseTimePaint").setText(sunriseSunset.first);
        thirdRowPaints.get("sunsetTimePaint").setText(sunriseSunset.second);
    }

}
