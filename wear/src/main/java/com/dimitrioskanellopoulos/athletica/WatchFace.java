package com.dimitrioskanellopoulos.athletica;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Pair;
import android.util.TypedValue;

import com.dimitrioskanellopoulos.athletica.paints.SensorPaint;
import com.dimitrioskanellopoulos.athletica.paints.TextPaint;
import com.dimitrioskanellopoulos.athletica.paints.BatterySensorPaint;
import com.dimitrioskanellopoulos.athletica.paints.SensorPaintFactory;
import com.dimitrioskanellopoulos.athletica.paints.SunriseSunsetPaint;

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

    // Standard Paints -> Time and Battery
    private final LinkedHashMap<String, TextPaint> standardPaints = new LinkedHashMap<String, TextPaint>();

    // Extra Paints -> Date for now
    private final LinkedHashMap<String, TextPaint> extraPaints = new LinkedHashMap<String, TextPaint>();

    // Sensor Paints
    private final LinkedHashMap<Integer, SensorPaint> sensorPaints = new LinkedHashMap<Integer, SensorPaint>();

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
        timePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        timePaint.setColor(DATE_AND_TIME_DEFAULT_COLOUR);
        timePaint.setTextSize(resources.getDimension(R.dimen.time_size));
        timePaint.setAntiAlias(true);
        standardPaints.put("timePaint", timePaint);

        // Add paint for battery level
        BatterySensorPaint batterySensorPaint = new BatterySensorPaint();
        batterySensorPaint.setTypeface(fontAwesome);
        batterySensorPaint.setColor(TEXT_DEFAULT_COLOUR);
        batterySensorPaint.setTextSize(resources.getDimension(R.dimen.battery_text_size));
        batterySensorPaint.setAntiAlias(true);
        batterySensorPaint.setText("0");
        standardPaints.put("batterySensorPaint", batterySensorPaint);

        // Add paint for date
        TextPaint datePaint = new TextPaint();
        datePaint.setColor(DATE_AND_TIME_DEFAULT_COLOUR);
        datePaint.setTextSize(resources.getDimension(R.dimen.date_size));
        datePaint.setAntiAlias(true);
        datePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        extraPaints.put("datePaint", datePaint);

        // Add paint for sunrise and sunset ( can be divided)
        TextPaint sunriseSunsetPaint = new SunriseSunsetPaint();
        sunriseSunsetPaint.setTypeface(fontAwesome);
        sunriseSunsetPaint.setColor(TEXT_DEFAULT_COLOUR);
        sunriseSunsetPaint.setTextSize(resources.getDimension(R.dimen.text_size));
        sunriseSunsetPaint.setAntiAlias(true);
        extraPaints.put("sunriseSunsetPaint", sunriseSunsetPaint);
    }

    /**
     * @todo maybe cache calculations
     */
    public void draw(Canvas canvas, Rect bounds) {

        // Update time
        calendar.setTimeInMillis(System.currentTimeMillis());

        // First draw background
        canvas.drawRect(0, 0, bounds.width(), bounds.height(), backgroundPaint);

        // Draw Time
        TextPaint timePaint = standardPaints.get("timePaint");
        timePaint.setText(String.format(
                shouldShowSeconds ?
                        TIME_FORMAT_WITH_SECONDS :
                        TIME_FORMAT_WITHOUT_SECONDS,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND)));
        canvas.drawText(timePaint.getText(), computeXOffset(timePaint, bounds), computeFirstRowYOffset(timePaint, bounds), timePaint);

        // Draw battery
        TextPaint batterySensorPaint = standardPaints.get("batterySensorPaint");
        canvas.drawText(batterySensorPaint.getText(), computeXOffset(batterySensorPaint, bounds), computeLastRowYOffset(batterySensorPaint, bounds), batterySensorPaint);

        // Set the text of the data
        extraPaints.get("datePaint").setText(String.format(DATE_FORMAT, calendar.get(calendar.DAY_OF_MONTH), calendar.get(calendar.MONTH), calendar.get(calendar.YEAR)));

        Float yOffset = computeFirstRowYOffset(timePaint, bounds);
        // Go over the extra paints
        for (Map.Entry<String, TextPaint> entry : extraPaints.entrySet()) {
            TextPaint paint = entry.getValue();
            if (paint.getText() == null) {
                continue;
            }
            yOffset = yOffset + rowVerticalMargin + computeRowYOffset(paint);
            Float xOffset = computeXOffset(paint, bounds);
            canvas.drawText(paint.getText(), xOffset, yOffset, paint);
        }
        // Go over the sensor paints
        for (Map.Entry<Integer, SensorPaint> entry : sensorPaints.entrySet()) {
            TextPaint paint = entry.getValue();
            if (paint.getText() == null) {
                continue;
            }
            yOffset = yOffset + rowVerticalMargin + computeRowYOffset(paint);
            Float xOffset = computeXOffset(paint, bounds);
            canvas.drawText(paint.getText(), xOffset, yOffset, paint);
        }
    }

    /**
     * Computes the X-Axis offset so that the text is horizontically centered
     */
    private float computeXOffset(TextPaint paint, Rect watchBounds) {
        return watchBounds.exactCenterX() - (paint.measureText(paint.getText()) / 2.0f);
    }

    /**
     * Computes the Y-Axis offset for the first row based on the exact center of the screen
     */
    private float computeFirstRowYOffset(TextPaint firstRowPaint, Rect watchBounds) {
        float centerY = watchBounds.exactCenterY();
        Rect textBounds = new Rect();
        firstRowPaint.getTextBounds(firstRowPaint.getText(), 0, firstRowPaint.getText().length(), textBounds);
        int textHeight = textBounds.height();
        return centerY + (textHeight / 2.0f);
    }

    /**
     * Computes the Y-Axis offset for a paint, according to it's size and margin
     */
    private float computeRowYOffset(TextPaint paint) {
        Rect textBounds = new Rect();
        paint.getTextBounds(paint.getText(), 0, paint.getText().length(), textBounds);
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
     * Toggles the antialias for all the paints
     */
    public void setAntiAlias(boolean antiAlias) {
        for (Map.Entry<String, TextPaint> entry : standardPaints.entrySet()) {
            entry.getValue().setAntiAlias(antiAlias);
        }
        for (Map.Entry<String, TextPaint> entry : extraPaints.entrySet()) {
            entry.getValue().setAntiAlias(antiAlias);
        }
        for (Map.Entry<Integer, SensorPaint> entry : sensorPaints.entrySet()) {
            entry.getValue().setAntiAlias(antiAlias);
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
        SensorPaint sensorPaint = SensorPaintFactory.getPaintForSensorType(sensorType);
        sensorPaint.setTypeface(fontAwesome);
        sensorPaint.setColor(TEXT_DEFAULT_COLOUR);
        sensorPaint.setTextSize(resources.getDimension(R.dimen.text_size));
        sensorPaint.setAntiAlias(true);
        sensorPaints.put(sensorType, sensorPaint);
    }

    public void removeSensorPaint(Integer key) {
        sensorPaints.remove(key);
    }

    public void updateSensorPaintText(Integer key, String value) {
        sensorPaints.get(key).setText(value);
    }

    public void updateBatteryLevel(Integer batteryPercentage) {
        standardPaints.get("batterySensorPaint").setText(batteryPercentage.toString());
    }

    public void updateSunriseSunset(Pair<String, String> sunriseSunset) {
        extraPaints.get("sunriseSunsetPaint").setText(sunriseSunset.first + "-" + sunriseSunset.second);
    }

}
