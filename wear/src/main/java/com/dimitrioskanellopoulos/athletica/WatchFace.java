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

import com.dimitrioskanellopoulos.athletica.matrix.columns.AmPmColumn;
import com.dimitrioskanellopoulos.athletica.matrix.columns.BatteryIconColumn;
import com.dimitrioskanellopoulos.athletica.matrix.columns.Column;
import com.dimitrioskanellopoulos.athletica.matrix.columns.ColumnFactory;
import com.dimitrioskanellopoulos.athletica.matrix.columns.DateColumn;
import com.dimitrioskanellopoulos.athletica.matrix.columns.TimeColumn;
import com.dimitrioskanellopoulos.athletica.matrix.rows.Row;

import java.util.TimeZone;

public class WatchFace {
    private static final String TAG = "Watchface";

    private static final int DEFAULT_COLOUR = Color.WHITE;
    private static final int BACKGROUND_DEFAULT_COLOUR = Color.BLACK;
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
    private final static Row[] rows = {firstRow, secondRow, thirdRow, forthRow, fifthRow};
    private final Resources resources;
    // Background Paint
    private final Paint backgroundPaint;
    private final Float horizontalMargin;
    private final Typeface fontAwesome;
    private Typeface defaultTypeface;
    private boolean isRound;
    private boolean isInAmbientMode = false;
    private boolean interlace = true;
    private boolean invertBlackAndWhite = false;

    private int chinSize;

    /**
     * The WatchFace. Everything the user sees. No extra init or data manipulation
     */
    public WatchFace(Context context) {

        resources = context.getApplicationContext().getResources();

        // Define the margin of the rows for vertical
        Float verticalMargin = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                resources.getDimension(R.dimen.row_vertical_margin),
                resources.getDisplayMetrics());

        // Define the margin of the rows for horizontal
        horizontalMargin = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                resources.getDimension(R.dimen.row_horizontal_margin),
                resources.getDisplayMetrics());

        // Set margins to the rows
        firstRow.setVerticalMargin(0.0f);
        secondRow.setVerticalMargin(verticalMargin);
        thirdRow.setVerticalMargin(verticalMargin);
        forthRow.setVerticalMargin(verticalMargin);
        fifthRow.setVerticalMargin(verticalMargin);

        // Default typeface
        defaultTypeface = Typeface.SANS_SERIF;

        // Add paint for background
        backgroundPaint = new Paint();
        backgroundPaint.setColor(BACKGROUND_DEFAULT_COLOUR);

        // Add FontAwesome paint for icons
        fontAwesome = Typeface.createFromAsset(context.getAssets(), "fonts/fontawesome-webfont.ttf");

        // Add column for time
        addColumnForTime();

        // Add column for date
        addColumnForDate();

        // Add column for sunrise
        addColumnForSunrise();

        // Add column for sunset
        addColumnForSunset();

        // Add column for battery level
        addColumnForBattery();
    }

    private void addColumnForTime() {
        TimeColumn timeColumn = new TimeColumn(defaultTypeface, resources.getDimension(R.dimen.time_size), getDefaultColor());
        firstRow.addColumn("time", timeColumn);
    }

    private int getDefaultColor() {
        return !invertBlackAndWhite ? DEFAULT_COLOUR : BACKGROUND_DEFAULT_COLOUR;
    }

    private void addColumnForDate() {
        DateColumn dateColumn = new DateColumn(defaultTypeface, resources.getDimension(R.dimen.date_size), getDefaultColor());
        secondRow.addColumn("date", dateColumn);
    }

    private void addColumnForSunrise() {
        Column sunriseIconColumn = new Column(fontAwesome, resources.getDimension(R.dimen.icon_size), getDefaultColor());
        sunriseIconColumn.setText(resources.getString(R.string.icon_sunrise));
        sunriseIconColumn.setHorizontalMargin(horizontalMargin);
        thirdRow.addColumn("sunrise_icon", sunriseIconColumn);

        Column sunriseColumn = new Column(defaultTypeface, resources.getDimension(R.dimen.text_size), getDefaultColor());
        sunriseColumn.setHorizontalMargin(horizontalMargin);
        thirdRow.addColumn("sunrise", sunriseColumn);
    }

    private void addColumnForSunset() {
        Column sunsetIconColumn = new Column(fontAwesome, resources.getDimension(R.dimen.icon_size), getDefaultColor());
        sunsetIconColumn.setHorizontalMargin(horizontalMargin);
        sunsetIconColumn.setText(resources.getString(R.string.icon_sunset));
        thirdRow.addColumn("sunset_icon", sunsetIconColumn);

        Column sunsetColumn = new Column(defaultTypeface, resources.getDimension(R.dimen.text_size), getDefaultColor());
        thirdRow.addColumn("sunset", sunsetColumn);
    }

    private void addColumnForBattery() {
        BatteryIconColumn batteryIconColumn = new BatteryIconColumn(resources, fontAwesome, resources.getDimension(R.dimen.icon_size), getDefaultColor());
        batteryIconColumn.setHorizontalMargin(horizontalMargin);
        fifthRow.addColumn("battery_icon", batteryIconColumn);

        Column batteryColumn = new Column(defaultTypeface, resources.getDimension(R.dimen.battery_text_size), getDefaultColor());
        fifthRow.addColumn("battery", batteryColumn);
    }

    public void draw(Canvas canvas, Rect bounds) {

        // First draw background
        canvas.drawRect(0, 0, bounds.width(), bounds.height(), backgroundPaint);

        drawRows(canvas, bounds);

        if (interlace) {
            interlaceCanvas(canvas, bounds);
        }
    }

    /**
     * @todo document more and make it faster
     */
    public void drawRows(Canvas canvas, Rect bounds) {
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
            Float maxColumnHeight = 0f;
            // Go over the paints (columns of each row)
            int columnCount = 0;
            for (Column column : row.getAllColumns()) {
                columnCount++;
                // If the height is bigger than the current set it to that
                if (column.getHeight() > maxColumnHeight) {
                    maxColumnHeight = column.getHeight();
                }
                // The total width of the row increases by the Column's text with
                totalTextWidth += column.getWidth() + column.getHorizontalMargin();
                // Remove the horizontal margin if it's the last column
                if (columnCount >= row.getAllColumns().length) {
                    // Log.d(TAG, "Removing last column margin " + column.getHorizontalMargin());
                    totalTextWidth -= column.getHorizontalMargin();
                }
                // Log.d(TAG, "Row " + rowCount + " Column " + columnCount + " height "+ column.getHeight());
            }
            // Add the total height to the offset
            yOffset += row.getVerticalMargin() + maxColumnHeight / 2.0f;
            // Last row change yOffset and put it as low as possible because it's the bottom row
            if (rowCount == rows.length - 1) {
                yOffset = bounds.bottom - chinSize - maxColumnHeight / 2.0f;
            }

            /**
             * All is found and set start drawing
             */
            Float cursor = bounds.exactCenterX() - totalTextWidth / 2.0f;
            for (Column column : row.getAllColumns()) {
                // Draw the column
                canvas.drawText(column.getText(), cursor, yOffset, column.getPaint()); // check if it needs per column height
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
        for (Row row : rows) {
            for (Column column : row.getAllColumns()) {
                column.setAmbientMode(inAmbientMode);
            }
        }
    }


    public void setTimeFormat24(Boolean timeFormat24) {
        TimeColumn timeColumn = (TimeColumn) firstRow.getColumn("time");
        timeColumn.setTimeFormat24(timeFormat24);
        timeColumn.getPaint().setTextSize(timeFormat24 ?
                resources.getDimension(R.dimen.time_size) :
                resources.getDimension(R.dimen.time_size) - resources.getDimension(R.dimen.time_am_pm_size));

        if (timeFormat24) {
            firstRow.removeColumn("amPm");
        } else {
            AmPmColumn amPmColumn = new AmPmColumn(defaultTypeface, resources.getDimension(R.dimen.time_am_pm_size), getDefaultColor());
            firstRow.addColumn("amPm", amPmColumn);
        }
    }

    public void setShowDateNamesFormat(Boolean showDateNamesFormat) {
        DateColumn dateColumn = (DateColumn) secondRow.getColumn("date");
        dateColumn.setShowDateNamesFormat(showDateNamesFormat);
    }

    public void shouldInterlace(Boolean shouldInterlace) {
        this.interlace = shouldInterlace;
    }

    public void setInvertBlackAndWhite(Boolean invertBlackAndWhite) {
        backgroundPaint.setColor(!invertBlackAndWhite ? BACKGROUND_DEFAULT_COLOUR : DEFAULT_COLOUR);
        for (Row row : rows) {
            for (Column column : row.getAllColumns()) {
                column.setTextDefaultColor(!invertBlackAndWhite ? DEFAULT_COLOUR : BACKGROUND_DEFAULT_COLOUR);
            }
        }
        this.invertBlackAndWhite = invertBlackAndWhite;
    }

    public void updateTimeZoneWith(TimeZone timeZone) {
        Log.d(TAG, "Updating timezone to " + timeZone.getDisplayName());
        ((TimeColumn) firstRow.getColumn("time")).setTimezone(timeZone);
        ((DateColumn) secondRow.getColumn("date")).setTimezone(timeZone);
    }

    public void setIsRound(boolean round) {
        isRound = round;
    }

    public void setChinSize(Integer chinSize) {
        this.chinSize = chinSize;
    }

    public void addSensorColumn(Integer sensorType) {
        Column sensorIconColumn = ColumnFactory.getIconColumnForSensorType(resources, sensorType, fontAwesome, resources.getDimension(R.dimen.icon_size), getDefaultColor());
        sensorIconColumn.setHorizontalMargin(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                resources.getDimension(R.dimen.icon_margin),
                resources.getDisplayMetrics()));
        forthRow.addColumn(sensorType.toString() + "_icon", sensorIconColumn);

        Column sensorColumn = new Column(defaultTypeface, resources.getDimension(R.dimen.text_size), getDefaultColor());
        sensorColumn.setHorizontalMargin(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                resources.getDimension(R.dimen.units_margin),
                resources.getDisplayMetrics()));
        forthRow.addColumn(sensorType.toString(), sensorColumn);

        Column sensorUnitsColumn = ColumnFactory.getUnitsColumnForSensorType(resources, sensorType, defaultTypeface, resources.getDimension(R.dimen.units_size), getDefaultColor());
        // @todo should remove the margin here if it detects it's the last
        sensorUnitsColumn.setHorizontalMargin(horizontalMargin);
        forthRow.addColumn(sensorType.toString() + "_units", sensorUnitsColumn);
    }

    public void removeSensorPaint(Integer sensorType) {
        forthRow.removeColumn(sensorType.toString() + "_icon");
        forthRow.removeColumn(sensorType.toString());
        forthRow.removeColumn(sensorType.toString() + "_units");
    }

    public void updateSensorPaintText(Integer sensorType, String value) {
        forthRow.getColumn(sensorType.toString()).setText(value);
    }

    public void updateBatteryLevel(Integer batteryPercentage) {
        ((BatteryIconColumn) fifthRow.getColumn("battery_icon")).setBatteryLevel(batteryPercentage);
        fifthRow.getColumn("battery").setText(batteryPercentage.toString() + "%");
    }

    public void updateSunriseSunset(Pair<String, String> sunriseSunset) {
        thirdRow.getColumn("sunrise").setText(sunriseSunset.first);
        thirdRow.getColumn("sunset").setText(sunriseSunset.second);
    }
}
