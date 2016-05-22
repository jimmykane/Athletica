package com.dimitrioskanellopoulos.athletica;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;
import android.util.Pair;

import com.dimitrioskanellopoulos.athletica.grid.Grid;
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

    private static final Grid grid = new Grid();

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

        grid.putRow("firstRow", new Row());
        grid.putRow("secondRow", new Row());
        grid.putRow("thirdRow", new Row());
        grid.putRow("forthRow", new Row());
        grid.putRow("fifthRow", new Row());

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

        GridRenderer.renderGrid(canvas, bounds, grid, chinSize, true, backgroundColor);

        if (interlace) {
            GridRenderer.interlaceCanvas(canvas, bounds, Color.BLACK, isInAmbientMode ? 100 : 70);
        }
    }

    private void addColumnForTime() {
        TimeColumn timeColumn = new TimeColumn(defaultTypeface, resources.getDimension(R.dimen.time_size), textColor);
        grid.getRow("firstRow").putColumn("time", timeColumn);
    }

    private void addColumnForDate() {
        DateColumn dateColumn = new DateColumn(defaultTypeface, resources.getDimension(R.dimen.date_size), textColor);
        grid.getRow("secondRow").putColumn("date", dateColumn);
    }

    private void addColumnForSunrise() {
        Column sunriseIconColumn = new Column(fontAwesome, resources.getDimension(R.dimen.icon_size), textColor);
        sunriseIconColumn.setText(resources.getString(R.string.icon_sunrise));
        sunriseIconColumn.setHorizontalMargin(resources.getDimension(R.dimen.icon_margin));
        grid.getRow("thirdRow").putColumn("sunrise_icon", sunriseIconColumn);

        Column sunriseColumn = new Column(defaultTypeface, resources.getDimension(R.dimen.text_size), textColor);
        sunriseColumn.setHorizontalMargin(resources.getDimension(R.dimen.column_margin));
        grid.getRow("thirdRow").putColumn("sunrise", sunriseColumn);
    }

    private void addColumnForSunset() {
        Column sunsetIconColumn = new Column(fontAwesome, resources.getDimension(R.dimen.icon_size), textColor);
        sunsetIconColumn.setHorizontalMargin(resources.getDimension(R.dimen.icon_margin));
        sunsetIconColumn.setText(resources.getString(R.string.icon_sunset));
        grid.getRow("thirdRow").putColumn("sunset_icon", sunsetIconColumn);

        Column sunsetColumn = new Column(defaultTypeface, resources.getDimension(R.dimen.text_size), textColor);
        grid.getRow("thirdRow").putColumn("sunset", sunsetColumn);
    }

    public void addSensorColumn(Integer sensorType) {
        Column sensorIconColumn = ColumnFactory.getIconColumnForSensorType(resources, sensorType, fontAwesome, resources.getDimension(R.dimen.icon_size), textColor);
        sensorIconColumn.setHorizontalMargin(resources.getDimension(R.dimen.icon_margin));
        grid.getRow("forthRow").putColumn(sensorType.toString() + "_icon", sensorIconColumn);

        Column sensorColumn = new Column(defaultTypeface, resources.getDimension(R.dimen.text_size), textColor);
        sensorColumn.setHorizontalMargin(resources.getDimension(R.dimen.units_margin));
        grid.getRow("forthRow").putColumn(sensorType.toString(), sensorColumn);

        Column sensorUnitsColumn = ColumnFactory.getUnitsColumnForSensorType(resources, sensorType, defaultTypeface, resources.getDimension(R.dimen.units_size), textColor);
        grid.getRow("forthRow").putColumn(sensorType.toString() + "_units", sensorUnitsColumn);
        // @todo  When using more than one sensors should add the margin to the previous one
    }

    private void addColumnForBattery() {
        BatteryIconColumn batteryIconColumn = new BatteryIconColumn(resources, fontAwesome, resources.getDimension(R.dimen.icon_size), textColor);
        batteryIconColumn.setHorizontalMargin(resources.getDimension(R.dimen.icon_margin));
        grid.getRow("fifthRow").putColumn("battery_icon", batteryIconColumn);

        Column batteryColumn = new Column(defaultTypeface, resources.getDimension(R.dimen.battery_text_size), textColor);
        grid.getRow("fifthRow").putColumn("battery", batteryColumn);
    }

    /**
     * Toggles the ambient or not mode for all the paints
     */
    public void inAmbientMode(boolean inAmbientMode) {
        isInAmbientMode = inAmbientMode;
        for (Row row : grid.getAllRows()) {
            for (Column column : row.getAllColumns()) {
                column.setAmbientMode(inAmbientMode);
            }
        }
    }

    public void setTimeFormat24(Boolean timeFormat24) {
        TimeColumn timeColumn = (TimeColumn) grid.getRow("firstRow").getColumn("time");
        timeColumn.setTimeFormat24(timeFormat24);
        timeColumn.getPaint().setTextSize(timeFormat24 ?
                resources.getDimension(R.dimen.time_size) :
                resources.getDimension(R.dimen.time_size) - resources.getDimension(R.dimen.time_am_pm_size));

        if (timeFormat24) {
            grid.getRow("firstRow").removeColumn("amPm");
        } else {
            AmPmColumn amPmColumn = new AmPmColumn(defaultTypeface, resources.getDimension(R.dimen.time_am_pm_size), textColor);
            grid.getRow("firstRow").putColumn("amPm", amPmColumn);
        }
    }

    public void setShowDateNamesFormat(Boolean showDateNamesFormat) {
        DateColumn dateColumn = (DateColumn) grid.getRow("secondRow").getColumn("date");
        dateColumn.setShowDateNamesFormat(showDateNamesFormat);
    }

    public void shouldInterlace(Boolean shouldInterlace) {
        this.interlace = shouldInterlace;
    }

    public void setInvertBlackAndWhite(Boolean invertBlackAndWhite) {
        textColor = invertBlackAndWhite ? Color.BLACK : Color.WHITE;
        backgroundColor = invertBlackAndWhite ? Color.WHITE : Color.BLACK;
        for (Row row : grid.getAllRows()) {
            for (Column column : row.getAllColumns()) {
                column.setTextDefaultColor(textColor);
            }
        }
        this.invertBlackAndWhite = invertBlackAndWhite;
    }

    public void updateTimeZoneWith(TimeZone timeZone) {
        Log.d(TAG, "Updating timezone to " + timeZone.getDisplayName());
        ((TimeColumn) grid.getRow("firstRow").getColumn("time")).setTimezone(timeZone);
        ((DateColumn) grid.getRow("secondRow").getColumn("date")).setTimezone(timeZone);
    }

    public void setIsRound(boolean round) {
        isRound = round;
    }

    public void setChinSize(Integer chinSize) {
        this.chinSize = chinSize;
    }

    public void removeSensor(Integer sensorType) {
        grid.getRow("forthRow").removeColumn(sensorType.toString() + "_icon");
        grid.getRow("forthRow").removeColumn(sensorType.toString());
        grid.getRow("forthRow").removeColumn(sensorType.toString() + "_units");
    }

    public void updateSensorText(Integer sensorType, String value) {
        grid.getRow("forthRow").getColumn(sensorType.toString()).setText(value);
    }

    public void updateBatteryLevel(Integer batteryPercentage) {
        ((BatteryIconColumn) grid.getRow("fifthRow").getColumn("battery_icon")).setBatteryLevel(batteryPercentage);
        grid.getRow("fifthRow").getColumn("battery").setText(batteryPercentage.toString() + "%");
    }

    public void updateSunriseSunset(Pair<String, String> sunriseSunset) {
        grid.getRow("thirdRow").getColumn("sunrise").setText(sunriseSunset.first);
        grid.getRow("thirdRow").getColumn("sunset").setText(sunriseSunset.second);
    }
}
