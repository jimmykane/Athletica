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

import com.dimitrioskanellopoulos.athletica.matrix.columns.Column;
import com.dimitrioskanellopoulos.athletica.matrix.columns.TimeColumn;
import com.dimitrioskanellopoulos.athletica.matrix.rows.Row;
import com.dimitrioskanellopoulos.athletica.paints.SensorPaint;
import com.dimitrioskanellopoulos.athletica.paints.SunsetTimePaint;
import com.dimitrioskanellopoulos.athletica.paints.TextPaint;
import com.dimitrioskanellopoulos.athletica.paints.BatterySensorPaint;
import com.dimitrioskanellopoulos.athletica.paints.SensorPaintFactory;
import com.dimitrioskanellopoulos.athletica.paints.SunriseTimePaint;
import com.dimitrioskanellopoulos.athletica.paints.TimePaint;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

public class WatchFace {
    private static final String TAG = "Watchface";

    private static final int DATE_AND_TIME_DEFAULT_COLOUR = Color.WHITE;
    private static final int TEXT_DEFAULT_COLOUR = Color.WHITE;
    private static final int BACKGROUND_DEFAULT_COLOUR = Color.BLACK;

    private final Resources resources;

    // The Calendar
    private static final Calendar calendar = Calendar.getInstance();

    // Background Paint
    private final Paint backgroundPaint;

    // First row of paints

    private final static Row firstRow = new Row();

    // Second row

    private final static Row secondRow = new Row();

    // Third row

    private final static Row thirdRow = new Row();

    // Forth row

    private final static Row forthRow = new Row();

    // Last row

    private final static Row fifthRow = new Row();

    // All the rows together

    // Convert to rows
    private final static Row[] rows = {firstRow, secondRow, thirdRow, forthRow, fifthRow};

    // FontAwesome
    private final TextPaint fontAwesomePaint;

    private Typeface defaultTypeface;

    private boolean isRound;
    private boolean isInAmbientMode;
    private int chinSize;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    private boolean shouldInterlace = true;

    /**
     * The WatchFace. Everything the user sees. No extra init or data manipulation
     */
    public WatchFace(Context context) {

        resources = context.getApplicationContext().getResources();

        // Define the margin of the rows for vertical
        Float rowVerticalMargin = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                resources.getDimension(R.dimen.row_vertical_margin),
                resources.getDisplayMetrics());

        // Define the margin of the rows for horizontal
        Float rowHorizontalMargin = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                resources.getDimension(R.dimen.row_horizontal_margin),
                resources.getDisplayMetrics());

        // Set margins to the rows
        firstRow.setVerticalMargin(0.0f);
        secondRow.setVerticalMargin(rowVerticalMargin);
        thirdRow.setVerticalMargin(rowVerticalMargin);
        forthRow.setVerticalMargin(rowVerticalMargin);
        fifthRow.setVerticalMargin(rowVerticalMargin);


        // Default typeface
        defaultTypeface = Typeface.SANS_SERIF;

        // Add paint for background
        backgroundPaint = new Paint();
        backgroundPaint.setColor(BACKGROUND_DEFAULT_COLOUR);

        // Add FontAwesome paint for icons
        fontAwesomePaint = new TextPaint();
        fontAwesomePaint.setColor(TEXT_DEFAULT_COLOUR);
        fontAwesomePaint.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/fontawesome-webfont.ttf"));
        fontAwesomePaint.setTextSize(resources.getDimension(R.dimen.icon_size));

        // Add paint for time
        TimePaint timePaint = new TimePaint();
        timePaint.setTypeface(defaultTypeface);
        timePaint.setColor(DATE_AND_TIME_DEFAULT_COLOUR);
        timePaint.setTextSize(resources.getDimension(R.dimen.time_size));

        TimeColumn timeColumn = new TimeColumn();
        timeColumn.setPaint(timePaint);
        timeColumn.getPaint().setTextSize(resources.getDimension(R.dimen.time_size));
        firstRow.addColumn("time", timeColumn);

        // Add paint for date
        TextPaint datePaint = new TextPaint();
        datePaint.setColor(DATE_AND_TIME_DEFAULT_COLOUR);
        datePaint.setTypeface(defaultTypeface);
        datePaint.setTextSize(resources.getDimension(R.dimen.date_size));

        Column dateColumn = new Column();
        dateColumn.setPaint(datePaint);
        dateColumn.getPaint().setTextSize(resources.getDimension(R.dimen.date_size));
        secondRow.addColumn("date", dateColumn);

        // Add paint for sunrise
        SensorPaint sunriseTimePaint = new SunriseTimePaint();
        sunriseTimePaint.setColor(TEXT_DEFAULT_COLOUR);
        sunriseTimePaint.setTypeface(defaultTypeface);
        sunriseTimePaint.setTextSize(resources.getDimension(R.dimen.text_size));
        sunriseTimePaint.setIconTextPaint(fontAwesomePaint);

        Column sunriseColumn = new Column();
        sunriseColumn.setPaint(sunriseTimePaint);
        sunriseColumn.setHorizontalMargin(rowHorizontalMargin);
        sunriseColumn.getPaint().setTextSize(resources.getDimension(R.dimen.text_size));
        thirdRow.addColumn("sunrise", sunriseColumn);

        // Add paint for sunset
        SensorPaint sunsetTimePaint = new SunsetTimePaint();
        sunsetTimePaint.setColor(TEXT_DEFAULT_COLOUR);
        sunsetTimePaint.setTextSize(resources.getDimension(R.dimen.text_size));
        sunsetTimePaint.setIconTextPaint(fontAwesomePaint);
        sunsetTimePaint.setTypeface(defaultTypeface);

        Column sunsetColumn = new Column();
        sunsetColumn.setPaint(sunsetTimePaint);
        sunriseColumn.setHorizontalMargin(rowHorizontalMargin);
        sunriseColumn.getPaint().setTextSize(resources.getDimension(R.dimen.text_size));
        thirdRow.addColumn("sunset", sunsetColumn);

        // Add paint for battery level
        BatterySensorPaint batterySensorPaint = new BatterySensorPaint();
        batterySensorPaint.setColor(TEXT_DEFAULT_COLOUR);
        batterySensorPaint.setTextSize(resources.getDimension(R.dimen.battery_text_size));
        batterySensorPaint.setIconTextPaint(fontAwesomePaint);
        batterySensorPaint.setTypeface(defaultTypeface);

        Column batteryColumn = new Column();
        batteryColumn.setPaint(batterySensorPaint);
        batteryColumn.getPaint().setTextSize(resources.getDimension(R.dimen.battery_text_size));
        fifthRow.addColumn("battery", batteryColumn);
    }

    public void draw(Canvas canvas, Rect bounds) {

        // First draw background
        canvas.drawRect(0, 0, bounds.width(), bounds.height(), backgroundPaint);

        secondRow.getColumn("date").setText(dateFormat.format(calendar.getTime()));

        drawMatrix(canvas, bounds);

        interlaceCanvas(canvas, bounds);
    }


    public void drawMatrix(Canvas canvas, Rect bounds) {
        /**
         * We loop over each row:
         * 1. Find the total width of the text so we can center the text on X
         * 2. Find the biggest height of the text so we can offset on Y
         * 3. Take care for special cases of first and last row
         */
        Float yOffset = bounds.exactCenterY();
        int rowCount = 0;
        for (Row row : rows) {
            Float totalTextWidth = 0f;
            Float maxTextHeight = 0f;
            // Go over the paints (columns of each row)
            for (Column column : row.getAllColumns()) {
                Log.d(TAG, "Column: " + column.getText());
                // If the height is bigger than the current set it to that
                if (column.getHeight() > maxTextHeight) {
                    maxTextHeight = column.getHeight();
                }
                // The total width of the row increases by the paint's text with
                totalTextWidth += column.getWidth() + column.getHorizontalMargin();
            }

            // Add the total height to the offset
            yOffset += row.getVerticalMargin() + maxTextHeight / 2.0f;
            // Last row change yOffset and put it as low as possible because it's the bottom row
            if (rowCount == rows.length - 1) {
                yOffset = bounds.bottom - chinSize - maxTextHeight / 2.0f;
            }

            /**
             * All is found and set start drawing
             */
            Float cursor = bounds.exactCenterX() - (totalTextWidth - row.getVerticalMargin()) / 2.0f;
            for (Column column : row.getAllColumns()) {
                // Draw the paint
                canvas.drawText(column.getText(), cursor, yOffset, column.getPaint()); // check if it needs per paint height
                cursor += column.getWidth() + column.getHorizontalMargin();
            }
            rowCount++;
        }
    }

    /**
     * Applies interlace effect
     */
    private void interlaceCanvas(Canvas canvas, Rect bounds) {
        Paint interlacePaint = new Paint();
        interlacePaint.setColor(Color.BLACK);
        interlacePaint.setAlpha(60);
        if (isInAmbientMode) {
            interlacePaint.setAlpha(100);
        }
        for (int y = 0; y < bounds.bottom; y += 2) {
            canvas.drawLine(0, y, bounds.right, y, interlacePaint);
        }
        for (int x = 0; x < bounds.right; x += 2) {
            canvas.drawLine(x, 0, x, bounds.bottom, interlacePaint);
        }
    }

    /**
     * Toggles the ambient or not mode for all the paints
     */
    public void inAmbientMode(boolean inAmbientMode) {
        isInAmbientMode = inAmbientMode;
        for (Row row: rows){
            for (Column column: row.getAllColumns()){
                column.setAmbientMode(inAmbientMode);
            }
        }
    }

    public void setTimeFormat24(Boolean timeFormat24) {
        TimeColumn timeColumn = (TimeColumn) firstRow.getColumn("time");
        timeColumn.setTimeFormat24(timeFormat24);
        timeColumn.getPaint().setTextSize( timeFormat24 ?
                resources.getDimension(R.dimen.time_size) :
                resources.getDimension(R.dimen.time_size) - resources.getDimension(R.dimen.time_am_pm_size));

    }

    public void updateTimeZoneWith(TimeZone timeZone) {
        calendar.setTimeZone(timeZone);
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
        sensorPaint.setTypeface(defaultTypeface);

        Column sensorColumn = new Column();
        sensorColumn.setPaint(sensorPaint);
        sensorColumn.getPaint().setTextSize(resources.getDimension(R.dimen.text_size));
        forthRow.addColumn(sensorType.toString(), sensorColumn);
    }

    public void removeSensorPaint(Integer sensorType) {
        forthRow.removeColumn(sensorType.toString());
    }

    public void updateSensorPaintText(Integer sensorType, String value) {
        forthRow.getColumn(sensorType.toString()).setText(value);
    }

    public void updateBatteryLevel(Integer batteryPercentage) {
        fifthRow.getColumn("battery").setText(batteryPercentage.toString());
    }

    public void updateSunriseSunset(Pair<String, String> sunriseSunset) {
        thirdRow.getColumn("sunrise").setText(sunriseSunset.first);
        thirdRow.getColumn("sunset").setText(sunriseSunset.second);
    }
}
