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

import com.dimitrioskanellopoulos.athletica.paints.IconTextPaint;
import com.dimitrioskanellopoulos.athletica.paints.SensorPaint;
import com.dimitrioskanellopoulos.athletica.paints.SunsetTimePaint;
import com.dimitrioskanellopoulos.athletica.paints.TextPaint;
import com.dimitrioskanellopoulos.athletica.paints.BatterySensorPaint;
import com.dimitrioskanellopoulos.athletica.paints.SensorPaintFactory;
import com.dimitrioskanellopoulos.athletica.paints.SunriseTimePaint;

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

    // The Calendar
    private static final Calendar calendar = Calendar.getInstance();

    // Background Paint
    private final Paint backgroundPaint;

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
    private final static LinkedHashMap[] paintsRows = {firstRowPaints, secondRowPaints, thirdRowPaints, forthRowPaints, lastRowPaints};

    // FontAwesome
    private final IconTextPaint fontAwesomePaint;

    private final Float rowVerticalMargin;

    private final Float rowHorizontalMargin;

    private boolean shouldShowSeconds = true;

    private boolean isRound;
    private int chinSize;

    /**
     * The WatchFace. Everything the user sees. No extra init or data manipulation
     */
    public WatchFace(Context context) {

        resources = context.getResources();

        // Define the margin of the rows for vertical
        rowVerticalMargin = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                resources.getDimension(R.dimen.row_vertical_margin),
                resources.getDisplayMetrics());

        // Define the margin of the rows for horizontal
        rowHorizontalMargin = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                resources.getDimension(R.dimen.row_horizontal_margin),
                resources.getDisplayMetrics());

        // Add paint for background
        backgroundPaint = new Paint();
        backgroundPaint.setColor(BACKGROUND_DEFAULT_COLOUR);

        // Add FontAwesome paint for icons
        fontAwesomePaint = new IconTextPaint();
        fontAwesomePaint.setColor(TEXT_DEFAULT_COLOUR);
        fontAwesomePaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/fontawesome-webfont.ttf"));
        fontAwesomePaint.setTextSize(resources.getDimension(R.dimen.icon_size));

        // Add paint for time
        TextPaint timePaint = new TextPaint();
        timePaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL));
        timePaint.setColor(DATE_AND_TIME_DEFAULT_COLOUR);
        timePaint.setTextSize(resources.getDimension(R.dimen.time_size));
        firstRowPaints.put("timePaint", timePaint);

        // Add paint for date
        TextPaint datePaint = new TextPaint();
        datePaint.setColor(DATE_AND_TIME_DEFAULT_COLOUR);
        datePaint.setTextSize(resources.getDimension(R.dimen.date_size));
        datePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));

        secondRowPaints.put("datePaint", datePaint);

        // Add paint for sunrise
        SensorPaint sunriseTimePaint = new SunriseTimePaint();
        sunriseTimePaint.setColor(TEXT_DEFAULT_COLOUR);
        sunriseTimePaint.setTextSize(resources.getDimension(R.dimen.text_size));
        sunriseTimePaint.setIconTextPaint(fontAwesomePaint);
        thirdRowPaints.put("sunriseTimePaint", sunriseTimePaint);

        // Add paint for sunset
        SensorPaint sunsetTimePaint = new SunsetTimePaint();
        sunsetTimePaint.setColor(TEXT_DEFAULT_COLOUR);
        sunsetTimePaint.setTextSize(resources.getDimension(R.dimen.text_size));
        sunsetTimePaint.setIconTextPaint(fontAwesomePaint);
        thirdRowPaints.put("sunsetTimePaint", sunsetTimePaint);

        // Add paint for battery level
        BatterySensorPaint batterySensorPaint = new BatterySensorPaint();
        batterySensorPaint.setColor(TEXT_DEFAULT_COLOUR);
        batterySensorPaint.setTextSize(resources.getDimension(R.dimen.battery_text_size));
        batterySensorPaint.setIconTextPaint(fontAwesomePaint);
        lastRowPaints.put("batterySensorPaint", batterySensorPaint);
    }

    /**
     * @todo maybe cache calculations
     */
    public void draw(Canvas canvas, Rect bounds) {

        // Update time
        calendar.setTimeInMillis(System.currentTimeMillis());

        // First draw background
        canvas.drawRect(0, 0, bounds.width(), bounds.height(), backgroundPaint);

        // Draw Time for now
        TextPaint timePaint = firstRowPaints.get("timePaint");
        timePaint.setText(String.format(
                shouldShowSeconds ?
                        TIME_FORMAT_WITH_SECONDS :
                        TIME_FORMAT_WITHOUT_SECONDS,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                calendar.get(Calendar.SECOND)));

        // Set the text of the data
        TextPaint datePaint = secondRowPaints.get("datePaint");
        datePaint.setText(String.format(DATE_FORMAT, calendar.get(calendar.DAY_OF_MONTH), calendar.get(calendar.MONTH) + 1, calendar.get(calendar.YEAR)));

        // Draw Paints
        drawRows(canvas, bounds);
    }

    public void drawRows(Canvas canvas, Rect bounds){
        int i = 0;
        // Start at the center
        Float yOffset = bounds.exactCenterY();
        for (LinkedHashMap<String, TextPaint> paintsRow : paintsRows){
            Float totalTextWidth = 0f;
            Float maxTextHeight = 0f;
            for (Map.Entry<String, TextPaint> entry : paintsRow.entrySet()) {
                TextPaint textPaint = entry.getValue();
                totalTextWidth += textPaint.getSelfTextWidth() + rowHorizontalMargin;
                // If it's a sensor paint add the space for the icon with its own paint
                if (textPaint instanceof SensorPaint){
                    totalTextWidth += ((SensorPaint) textPaint).getIconTextPaint().measureText(((SensorPaint) textPaint).getIcon());
                }
                // @todo should check against the icon height as well
                if (maxTextHeight < textPaint.getSelfTextHeight()){
                    maxTextHeight = textPaint.getSelfTextHeight();
                }
            }

            yOffset += rowVerticalMargin + maxTextHeight/2.0f;
            // First row change yOffset
            if (i==0){
                yOffset = yOffset -rowHorizontalMargin;
            }
            // Last row change yOffset and put it as low as possible because it's the bottom row
            if (i == paintsRows.length -1){
                yOffset = bounds.bottom - chinSize - maxTextHeight/2.0f;
            }

            Float cursor = bounds.exactCenterX() - (totalTextWidth-rowHorizontalMargin)/2.0f;
            for (Map.Entry<String, TextPaint> entry : paintsRow.entrySet()) {
                TextPaint textPaint = entry.getValue();
                // Draw also the icon
                if (textPaint instanceof SensorPaint){
                    canvas.drawText(((SensorPaint) textPaint).getIcon(), cursor, yOffset  , ((SensorPaint) textPaint).getIconTextPaint());
                    cursor += ((SensorPaint) textPaint).getIconTextPaint().measureText(((SensorPaint) textPaint).getIcon()) + rowHorizontalMargin/2;
                }
                // Draw the paint
                canvas.drawText(textPaint.getText(), cursor, yOffset  , textPaint); // check if it needs per paint height
                cursor += textPaint.getSelfTextWidth() + rowHorizontalMargin;
            }
            i++;
        }
    }

    /**
     * Toggles the ambient or not mode for all the paints
     */
    public void inAmbientMode(boolean inAmbientMode) {
        for (LinkedHashMap<String, TextPaint> paintsRow : paintsRows){
            for (Map.Entry<String, TextPaint> entry : paintsRow.entrySet()) {
                entry.getValue().inAmbientMode(inAmbientMode);
            }
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
        sensorPaint.setIconTextPaint(fontAwesomePaint);
        sensorPaint.setColor(TEXT_DEFAULT_COLOUR);
        sensorPaint.setTextSize(resources.getDimension(R.dimen.text_size));
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
