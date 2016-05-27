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

import java.util.Map;
import java.util.TimeZone;

public class WatchFace {
    private static final String TAG = "Watchface";

    private static final Grid grid = new Grid();

    private final Context context;
    private final Resources resources;

    private final Typeface fontAwesome;
    private int textColor;
    private int backgroundColor;
    private Typeface defaultTypeface;
    private boolean isRound;
    private boolean ambientMode = false;
    private boolean isVisible = false;
    private boolean interlace = true;
    private boolean invertBlackAndWhite = false;

    private int chinSize;

    /**
     * The WatchFace. Everything the user sees. No extra init or data manipulation
     */
    public WatchFace(Context context) {

        this.context = context;
        resources = context.getResources();

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
            GridRenderer.interlaceCanvas(canvas, bounds, Color.BLACK, ambientMode ? 100 : 70);
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

    public Boolean hasSensorColumn(Integer sensorType) {
        if (grid.getRow("forthRow").getColumn(sensorType.toString()) != null) {
            return true;
        }
        return false;
    }

    public void addSensorColumn(Integer sensorType) {
        if (grid.getRow("forthRow").getAllColumns().size() >= 3) {
            grid.getRow("forthRow").getAllColumns().values().toArray(new Column[0])[grid.getRow("forthRow").getAllColumns().size() - 1].setHorizontalMargin(resources.getDimension(R.dimen.column_margin));
        }
        Column sensorIconColumn = ColumnFactory.getIconColumnForSensorType(resources, sensorType, fontAwesome, resources.getDimension(R.dimen.icon_size), textColor);
        sensorIconColumn.setHorizontalMargin(resources.getDimension(R.dimen.icon_margin));
        grid.getRow("forthRow").putColumn(sensorType.toString() + "_icon", sensorIconColumn);

        Column sensorColumn = ColumnFactory.getColumnForSensorType(context, sensorType, defaultTypeface, resources.getDimension(R.dimen.text_size), textColor);
        sensorColumn.setIsVisible(isVisible);
        sensorColumn.setHorizontalMargin(resources.getDimension(R.dimen.units_margin));
        grid.getRow("forthRow").putColumn(sensorType.toString(), sensorColumn);

        Column sensorUnitsColumn = ColumnFactory.getUnitsColumnForSensorType(resources, sensorType, defaultTypeface, resources.getDimension(R.dimen.units_size), textColor);
        grid.getRow("forthRow").putColumn(sensorType.toString() + "_units", sensorUnitsColumn);
        // @todo  When using more than one sensors should add the margin to the previous one
    }

    public void removeAllSensorColumns() {
        grid.getRow("forthRow").removeAllColumns();
    }

    public void removeSensorColumn(Integer sensorType) {
        // @todo should stop etc
        grid.getRow("forthRow").removeColumn(sensorType.toString() + "_icon");
        grid.getRow("forthRow").removeColumn(sensorType.toString());
        grid.getRow("forthRow").removeColumn(sensorType.toString() + "_units");
    }

    private void addColumnForBattery() {
        BatteryIconColumn batteryIconColumn = new BatteryIconColumn(resources, fontAwesome, resources.getDimension(R.dimen.icon_size), textColor);
        batteryIconColumn.setHorizontalMargin(resources.getDimension(R.dimen.icon_margin));
        grid.getRow("fifthRow").putColumn("battery_icon", batteryIconColumn);
        grid.getRow("fifthRow").setPaddingBottom(resources.getDimension(R.dimen.row_padding_bottom));

        Column batteryColumn = new Column(defaultTypeface, resources.getDimension(R.dimen.battery_text_size), textColor);
        grid.getRow("fifthRow").putColumn("battery", batteryColumn);
    }

    /**
     * Toggles the ambient or not mode for all the paints
     */
    public void setInAmbientMode(boolean inAmbientMode) {
        ambientMode = inAmbientMode;
        grid.setInAmbientMode(inAmbientMode);
    }

    /**
     * Toggles the visible or not mode for all the columns
     */
    public void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
        grid.setIsVisible(isVisible);
    }

    /**
     * Run's tasks for every column
     */
    public void runTasks() {
        // Fow now do it only on the sensor columns
        for (Map.Entry<String, Column> columnEntry : grid.getRow("forthRow").getAllColumns().entrySet()) {
            Column column = columnEntry.getValue();
            column.runTasks();
        }
    }

    public void setTimeFormat24(Boolean timeFormat24) {
        TimeColumn timeColumn = (TimeColumn) grid.getRow("firstRow").getColumn("time");
        timeColumn.setTimeFormat24(timeFormat24);

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
        for (Map.Entry<String, Row> rowEntry : grid.getAllRows().entrySet()) {
            Row row = rowEntry.getValue();
            for (Map.Entry<String, Column> columnEntry : row.getAllColumns().entrySet()) {
                Column column = columnEntry.getValue();
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

    public void updateBatteryLevel(Integer batteryPercentage) {
        ((BatteryIconColumn) grid.getRow("fifthRow").getColumn("battery_icon")).setBatteryLevel(batteryPercentage);
        grid.getRow("fifthRow").getColumn("battery").setText(batteryPercentage.toString() + "%");
    }

    public void updateSunriseSunset(Pair<String, String> sunriseSunset) {
        grid.getRow("thirdRow").getColumn("sunrise").setText(sunriseSunset.first);
        grid.getRow("thirdRow").getColumn("sunset").setText(sunriseSunset.second);
    }
}
