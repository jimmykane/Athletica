package com.dimitrioskanellopoulos.athletica;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.util.Pair;
import android.util.TypedValue;

import com.dimitrioskanellopoulos.athletica.grid.GridRenderer;
import com.dimitrioskanellopoulos.athletica.grid.columns.AmPmColumn;
import com.dimitrioskanellopoulos.athletica.grid.columns.BatteryIconColumn;
import com.dimitrioskanellopoulos.athletica.grid.columns.Column;
import com.dimitrioskanellopoulos.athletica.grid.columns.ColumnFactory;
import com.dimitrioskanellopoulos.athletica.grid.columns.DateColumn;
import com.dimitrioskanellopoulos.athletica.grid.columns.TimeColumn;
import com.dimitrioskanellopoulos.athletica.grid.rows.Row;

import java.util.TimeZone;

public class WatchFace {
    private static final String TAG = "Watchface";
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


    private final Typeface fontAwesome;
    private int textColor;
    private int backgroundColor;
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
        Float verticalMargin = resources.getDimension(R.dimen.row_vertical_margin);

        // Set margins to the rows
        firstRow.setVerticalMargin(0.0f);
        secondRow.setVerticalMargin(verticalMargin);
        thirdRow.setVerticalMargin(verticalMargin);
        forthRow.setVerticalMargin(verticalMargin);
        fifthRow.setVerticalMargin(verticalMargin);

        // Default typeface
        defaultTypeface = Typeface.SANS_SERIF;

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

    public void draw(Canvas canvas, Rect bounds) {

        GridRenderer.drawBackground(canvas, bounds, backgroundColor);

        GridRenderer.drawRows(canvas, bounds, rows, chinSize, true);

        if (interlace) {
            GridRenderer.interlaceCanvas(canvas, bounds, Color.BLACK, isInAmbientMode ? 100 : 70);
        }
    }

    private void addColumnForTime() {
        TimeColumn timeColumn = new TimeColumn(defaultTypeface, resources.getDimension(R.dimen.time_size), textColor);
        firstRow.addColumn("time", timeColumn);
    }

    private void addColumnForDate() {
        DateColumn dateColumn = new DateColumn(defaultTypeface, resources.getDimension(R.dimen.date_size), textColor);
        secondRow.addColumn("date", dateColumn);
    }

    private void addColumnForSunrise() {
        Column sunriseIconColumn = new Column(fontAwesome, resources.getDimension(R.dimen.icon_size), textColor);
        sunriseIconColumn.setText(resources.getString(R.string.icon_sunrise));
        sunriseIconColumn.setHorizontalMargin(resources.getDimension(R.dimen.icon_margin));
        thirdRow.addColumn("sunrise_icon", sunriseIconColumn);

        Column sunriseColumn = new Column(defaultTypeface, resources.getDimension(R.dimen.text_size), textColor);
        sunriseColumn.setHorizontalMargin(resources.getDimension(R.dimen.column_margin));
        thirdRow.addColumn("sunrise", sunriseColumn);
    }

    private void addColumnForSunset() {
        Column sunsetIconColumn = new Column(fontAwesome, resources.getDimension(R.dimen.icon_size), textColor);
        sunsetIconColumn.setHorizontalMargin(resources.getDimension(R.dimen.icon_margin));
        sunsetIconColumn.setText(resources.getString(R.string.icon_sunset));
        thirdRow.addColumn("sunset_icon", sunsetIconColumn);

        Column sunsetColumn = new Column(defaultTypeface, resources.getDimension(R.dimen.text_size), textColor);
        thirdRow.addColumn("sunset", sunsetColumn);
    }

    private void addColumnForBattery() {
        BatteryIconColumn batteryIconColumn = new BatteryIconColumn(resources, fontAwesome, resources.getDimension(R.dimen.icon_size), textColor);
        batteryIconColumn.setHorizontalMargin(resources.getDimension(R.dimen.icon_margin));
        fifthRow.addColumn("battery_icon", batteryIconColumn);

        Column batteryColumn = new Column(defaultTypeface, resources.getDimension(R.dimen.battery_text_size), textColor);
        fifthRow.addColumn("battery", batteryColumn);
    }

    public void addSensorColumn(Integer sensorType) {
        Column sensorIconColumn = ColumnFactory.getIconColumnForSensorType(resources, sensorType, fontAwesome, resources.getDimension(R.dimen.icon_size), textColor);
        sensorIconColumn.setHorizontalMargin(resources.getDimension(R.dimen.icon_margin));
        forthRow.addColumn(sensorType.toString() + "_icon", sensorIconColumn);

        Column sensorColumn = new Column(defaultTypeface, resources.getDimension(R.dimen.text_size), textColor);
        sensorColumn.setHorizontalMargin(resources.getDimension(R.dimen.units_margin));
        forthRow.addColumn(sensorType.toString(), sensorColumn);

        Column sensorUnitsColumn = ColumnFactory.getUnitsColumnForSensorType(resources, sensorType, defaultTypeface, resources.getDimension(R.dimen.units_size), textColor);
        forthRow.addColumn(sensorType.toString() + "_units", sensorUnitsColumn);
        // @todo  When using more than one sensors should add the margin to the previous one
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
            AmPmColumn amPmColumn = new AmPmColumn(defaultTypeface, resources.getDimension(R.dimen.time_am_pm_size), textColor);
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
        textColor = invertBlackAndWhite ? Color.BLACK : Color.WHITE;
        backgroundColor = invertBlackAndWhite ? Color.WHITE : Color.BLACK;
        for (Row row : rows) {
            for (Column column : row.getAllColumns()) {
                column.setTextDefaultColor(textColor);
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

    public void removeSensor(Integer sensorType) {
        forthRow.removeColumn(sensorType.toString() + "_icon");
        forthRow.removeColumn(sensorType.toString());
        forthRow.removeColumn(sensorType.toString() + "_units");
    }

    public void updateSensorText(Integer sensorType, String value) {
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
