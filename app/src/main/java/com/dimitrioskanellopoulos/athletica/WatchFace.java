package com.dimitrioskanellopoulos.athletica;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.util.Pair;
import android.util.TypedValue;

import com.dimitrioskanellopoulos.athletica.paints.AbstractSensorPaint;
import com.dimitrioskanellopoulos.athletica.paints.AbstractTextPaint;
import com.dimitrioskanellopoulos.athletica.paints.BatterySensorPaint;
import com.dimitrioskanellopoulos.athletica.paints.Paint;
import com.dimitrioskanellopoulos.athletica.paints.PressureSensorPaint;
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
    private final LinkedHashMap<String, AbstractTextPaint> standardPaints = new LinkedHashMap<String, AbstractTextPaint>();

    // Extra Paints -> Date for now
    private final LinkedHashMap<String, AbstractTextPaint> extraPaints = new LinkedHashMap<String, AbstractTextPaint>();

    // Sensor Paints
    private final LinkedHashMap<String, AbstractSensorPaint> sensorPaints = new LinkedHashMap<String, AbstractSensorPaint>();

    private final Float rowVerticalMargin;

    private boolean shouldShowSeconds = true;

    private boolean isRound;
    private int chinSize;

    /**
     * The WatchFace. Everything the user sees. No extra init or data manipulation
     * @param context
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
        backgroundPaint = new android.graphics.Paint();
        backgroundPaint.setColor(BACKGROUND_DEFAULT_COLOUR);

        // Add paint for time
        Paint timePaint = new Paint();
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
        standardPaints.put("batterySensorPaint", batterySensorPaint);

        // Add paint for date
        Paint datePaint = new Paint();
        datePaint.setColor(DATE_AND_TIME_DEFAULT_COLOUR);
        datePaint.setTextSize(resources.getDimension(R.dimen.date_size));
        datePaint.setAntiAlias(true);
        datePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        extraPaints.put("datePaint", datePaint);

        // Add paint for sunrise and sunset ( can be divided)
        AbstractSensorPaint sunriseSunsetPaint = new SunriseSunsetPaint();
        sunriseSunsetPaint.setTypeface(fontAwesome);
        sunriseSunsetPaint.setColor(TEXT_DEFAULT_COLOUR);
        sunriseSunsetPaint.setTextSize(resources.getDimension(R.dimen.text_size));
        sunriseSunsetPaint.setAntiAlias(true);
        sensorPaints.put("sunriseSunsetPaint", sunriseSunsetPaint);
    }

    /**
     * @todo should cache calcs
     * @param canvas
     * @param bounds
     */
    public void draw(Canvas canvas, Rect bounds) {

        // Update time
        calendar.setTimeInMillis(System.currentTimeMillis());

        // First draw background
        canvas.drawRect(0, 0, bounds.width(), bounds.height(), backgroundPaint);

        // Draw Time
        AbstractTextPaint timePaint = standardPaints.get("timePaint");
        timePaint.setText(String.format(
                shouldShowSeconds ?
                        TIME_FORMAT_WITH_SECONDS :
                        TIME_FORMAT_WITHOUT_SECONDS,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND)));
        canvas.drawText(timePaint.getText(), computeXOffset(timePaint, bounds), computeFirstRowYOffset(timePaint, bounds), timePaint);

        // Draw battery
        AbstractTextPaint batterySensorPaint = standardPaints.get("batterySensorPaint");
        if (batterySensorPaint.getText() != null) {
            canvas.drawText(batterySensorPaint.getText(), computeXOffset(batterySensorPaint, bounds), computeLastRowYOffset(batterySensorPaint, bounds), batterySensorPaint);
        }

        // Set the text of the data
        extraPaints.get("datePaint").setText(String.format(DATE_FORMAT, calendar.get(calendar.DAY_OF_MONTH), calendar.get(calendar.MONTH), calendar.get(calendar.YEAR)));

        Float yOffset = computeFirstRowYOffset(timePaint, bounds);
        // Go over the extra paints
        for (Map.Entry<String, AbstractTextPaint> entry : extraPaints.entrySet()) {
            AbstractTextPaint paint = entry.getValue();
            yOffset = yOffset + computeRowYOffset(paint);
            Float xOffset = computeXOffset(paint, bounds);
            canvas.drawText(paint.getText(), xOffset, yOffset, paint);
        }
        // Go over the sesnor paints
        for (Map.Entry<String, AbstractSensorPaint> entry : sensorPaints.entrySet()) {
            AbstractTextPaint paint = entry.getValue();
            yOffset = yOffset + computeRowYOffset(paint);
            Float xOffset = computeXOffset(paint, bounds);
            canvas.drawText(paint.getText(), xOffset, yOffset, paint);
        }
    }

    /**
     * Computes the X-Axis offset so that the text is horizontically centered
     * @param paint
     * @param watchBounds
     * @return
     */
    private float computeXOffset(AbstractTextPaint paint, Rect watchBounds) {
        return  watchBounds.exactCenterX() - (paint.measureText(paint.getText()) / 2.0f);
    }

    /**
     * Computes the Y-Axis offset for the first row based on the exact center of the screen
     * @param firstRowPaint
     * @param watchBounds
     * @return
     */
    private float computeFirstRowYOffset(AbstractTextPaint firstRowPaint, Rect watchBounds) {
        float centerY = watchBounds.exactCenterY() - rowVerticalMargin;
        Rect textBounds = new Rect();
        firstRowPaint.getTextBounds(firstRowPaint.getText(), 0, firstRowPaint.getText().length(), textBounds);
        int textHeight = textBounds.height();
        return centerY + (textHeight / 2.0f);
    }

    /**
     * Computs the Y-Axis offset for the last row based on the bottom of the screen
     * @param lastRowPaint
     * @param watchBounds
     * @return
     */
    private float computeLastRowYOffset(AbstractTextPaint lastRowPaint, Rect watchBounds) {
        Rect textBounds = new Rect();
        lastRowPaint.getTextBounds(lastRowPaint.getText(), 0, lastRowPaint.getText().length(), textBounds);
        return watchBounds.bottom - chinSize - textBounds.height() ;
    }

    /**
     * Computes the Y-Axis offset for a paint, according to it's size and margin
     * @param paint
     * @return
     */
    private float computeRowYOffset(AbstractTextPaint paint) {
        Rect textBounds = new Rect();
        paint.getTextBounds(paint.getText(), 0, paint.getText().length(), textBounds);
        return textBounds.height() + rowVerticalMargin;
    }

    /**
     * Toggles the antialias for all the paints
     * @param antiAlias
     */
    public void setAntiAlias(boolean antiAlias) {
        for (Map.Entry<String, AbstractTextPaint> entry : standardPaints.entrySet()) {
            entry.getValue().setAntiAlias(antiAlias);
        }

        for (Map.Entry<String, AbstractTextPaint> entry : extraPaints.entrySet()) {
            entry.getValue().setAntiAlias(antiAlias);
        }
        for (Map.Entry<String, AbstractSensorPaint> entry : sensorPaints.entrySet()) {
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

    public void createSensorPaint(Integer sensorType){
        switch (sensorType) {
            case Sensor.TYPE_PRESSURE:
                // Add paint for altitude
                AbstractSensorPaint altitudePaint = new PressureSensorPaint();
                altitudePaint.setTypeface(fontAwesome);
                altitudePaint.setColor(TEXT_DEFAULT_COLOUR);
                altitudePaint.setTextSize(resources.getDimension(R.dimen.text_size));
                altitudePaint.setAntiAlias(true);
                sensorPaints.put("pressureSensorPaint", altitudePaint);
                break;
        }
    }

    public void updateAltitude(String altitude) {
        sensorPaints.get("pressureSensorPaint").setText(altitude);
    }

    public void updateBatteryLevel(Integer batteryPercentage) {
        standardPaints.get("batterySensorPaint").setText(batteryPercentage.toString());
    }

    public void updateSunriseSunset(Pair<String, String> sunriseSunset) {
        sensorPaints.get("sunriseSunsetPaint").setText(sunriseSunset.first + "-" + sunriseSunset.second);
    }

}
