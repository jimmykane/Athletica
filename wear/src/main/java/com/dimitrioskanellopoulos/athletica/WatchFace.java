package com.dimitrioskanellopoulos.athletica;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.dimitrioskanellopoulos.athletica.grid.Grid;
import com.dimitrioskanellopoulos.athletica.grid.GridRenderer;
import com.dimitrioskanellopoulos.athletica.grid.columns.AmPmColumn;
import com.dimitrioskanellopoulos.athletica.grid.columns.BatteryIconColumn;
import com.dimitrioskanellopoulos.athletica.grid.columns.BatteryLevelColumn;
import com.dimitrioskanellopoulos.athletica.grid.columns.Column;
import com.dimitrioskanellopoulos.athletica.grid.columns.ColumnFactory;
import com.dimitrioskanellopoulos.athletica.grid.columns.DateColumn;
import com.dimitrioskanellopoulos.athletica.grid.columns.GoogleFitStepsColumn;
import com.dimitrioskanellopoulos.athletica.grid.columns.SunriseColumn;
import com.dimitrioskanellopoulos.athletica.grid.columns.SunsetColumn;
import com.dimitrioskanellopoulos.athletica.grid.columns.TimeColumn;
import com.dimitrioskanellopoulos.athletica.grid.rows.Row;
import com.dimitrioskanellopoulos.athletica.helpers.EmulatorHelper;
import com.dimitrioskanellopoulos.athletica.helpers.SunriseSunsetHelper;

import java.util.Map;

public class WatchFace {
    private static final String TAG = "Watchface";

    private static final Grid grid = new Grid();

    private final Context context;
    private final Resources resources;

    private final Typeface fontAwesome;
    private Typeface defaultTypeface;
    private boolean isRound;
    private boolean ambientMode = false;
    private boolean isVisible = false;
    private boolean interlace = true;
    private boolean dayNightMode = false;

    private int chinSize;
    private Boolean invertBlackAndWhite;

    /**
     * The WatchFace. Everything the user sees. No extra init or data manipulation
     */
    public WatchFace(Context context) {

        this.context = context;
        resources = context.getResources();

        grid.putRow("timeRow", new Row());
        grid.putRow("dateRow", new Row());
        grid.putRow("sunriseSunsetRow", new Row());
        grid.putRow("googleFitRow", new Row());
        grid.putRow("sensorsRow", new Row());
        grid.putRow("batteryRow", new Row());

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

        // Add column for sunset
        addColumnForGoogleFit();

        // Add column for battery level
        addColumnForBattery();
    }



    public void draw(Canvas canvas, Rect bounds) {

        GridRenderer.renderGrid(canvas, bounds, grid, chinSize);

        if (interlace) {
            GridRenderer.interlaceCanvas(canvas, bounds, Color.BLACK, ambientMode ? 100 : 70);
        }
    }

    private void addColumnForTime() {
        TimeColumn timeColumn = new TimeColumn(context, defaultTypeface, resources.getDimension(R.dimen.time_size), grid.getTextColor());
        grid.getRow("timeRow").putColumn("time", timeColumn);
    }

    private void addColumnForDate() {
        DateColumn dateColumn = new DateColumn(context, defaultTypeface, resources.getDimension(R.dimen.date_size), grid.getTextColor());
        grid.getRow("dateRow").putColumn("date", dateColumn);
    }

    private void addColumnForSunrise() {
        // @todo should be a calendar column
        Column sunriseIconColumn = new Column(context, fontAwesome, resources.getDimension(R.dimen.icon_size), grid.getTextColor());
        sunriseIconColumn.setText(resources.getString(R.string.icon_sunrise));
        sunriseIconColumn.setHorizontalMargin(resources.getDimension(R.dimen.icon_margin));
        grid.getRow("sunriseSunsetRow").putColumn("sunrise_icon", sunriseIconColumn);

        SunriseColumn sunriseColumn = new SunriseColumn(context, defaultTypeface, resources.getDimension(R.dimen.text_size), grid.getTextColor());
        sunriseColumn.setHorizontalMargin(resources.getDimension(R.dimen.column_margin));
        grid.getRow("sunriseSunsetRow").putColumn("sunrise", sunriseColumn);
    }

    private void addColumnForSunset() {
        Column sunsetIconColumn = new Column(context, fontAwesome, resources.getDimension(R.dimen.icon_size), grid.getTextColor());
        sunsetIconColumn.setHorizontalMargin(resources.getDimension(R.dimen.icon_margin));
        sunsetIconColumn.setText(resources.getString(R.string.icon_sunset));
        grid.getRow("sunriseSunsetRow").putColumn("sunset_icon", sunsetIconColumn);

        SunsetColumn sunsetColumn = new SunsetColumn(context, defaultTypeface, resources.getDimension(R.dimen.text_size), grid.getTextColor());
        grid.getRow("sunriseSunsetRow").putColumn("sunset", sunsetColumn);
    }

    private void addColumnForGoogleFit(){
        Column googleFitStepsIconColumn = new Column(context, fontAwesome, resources.getDimension(R.dimen.icon_size), grid.getTextColor());
        googleFitStepsIconColumn.setHorizontalMargin(resources.getDimension(R.dimen.icon_margin));
        googleFitStepsIconColumn.setText(resources.getString(R.string.icon_google_fit_steps));
        grid.getRow("googleFitRow").putColumn("googleFitSteps_icon", googleFitStepsIconColumn);

        GoogleFitStepsColumn googleFitStepsColumn = new GoogleFitStepsColumn(context, defaultTypeface, resources.getDimension(R.dimen.text_size), grid.getTextColor());
        grid.getRow("googleFitRow").putColumn("googleFitSteps", googleFitStepsColumn);
    }


    public Boolean hasSensorColumn(Integer sensorType) {
        if (grid.getRow("sensorsRow").getColumn(sensorType.toString()) != null) {
            return true;
        }
        return false;
    }

    public void addSensorColumn(Integer sensorType) {
        // Add icon
        if (grid.getRow("sensorsRow").getAllColumns().size() >= 3) {
            grid.getRow("sensorsRow").getAllColumns().values().toArray(new Column[0])[grid.getRow("sensorsRow").getAllColumns().size() - 1].setHorizontalMargin(resources.getDimension(R.dimen.column_margin));
        }
        Column sensorIconColumn = ColumnFactory.getIconColumnForSensorType(context, sensorType, fontAwesome, resources.getDimension(R.dimen.icon_size), grid.getTextColor());
        sensorIconColumn.setHorizontalMargin(resources.getDimension(R.dimen.icon_margin));
        grid.getRow("sensorsRow").putColumn(sensorType.toString() + "_icon", sensorIconColumn);

        // Add column depending on emu
        Column sensorColumn;
        if (EmulatorHelper.isEmulator()){
            sensorColumn = new Column(context, defaultTypeface, resources.getDimension(R.dimen.text_size), grid.getTextColor());
            sensorColumn.setText("60");
        }else {
            sensorColumn = ColumnFactory.getColumnForSensorType(context, sensorType, defaultTypeface, resources.getDimension(R.dimen.text_size), grid.getTextColor());
        }

        sensorColumn.setIsVisible(isVisible);
        sensorColumn.setHorizontalMargin(resources.getDimension(R.dimen.units_margin));
        grid.getRow("sensorsRow").putColumn(sensorType.toString(), sensorColumn);

        // Add units
        Column sensorUnitsColumn = ColumnFactory.getUnitsColumnForSensorType(context, sensorType, defaultTypeface, resources.getDimension(R.dimen.units_size), grid.getTextColor());
        grid.getRow("sensorsRow").putColumn(sensorType.toString() + "_units", sensorUnitsColumn);
    }

    public void removeAllSensorColumns() {
        grid.getRow("sensorsRow").removeAllColumns();
    }

    public void removeSensorColumn(Integer sensorType) {
        // @todo should stop etc
        grid.getRow("sensorsRow").removeColumn(sensorType.toString() + "_icon");
        grid.getRow("sensorsRow").removeColumn(sensorType.toString());
        grid.getRow("sensorsRow").removeColumn(sensorType.toString() + "_units");
    }

    private void addColumnForBattery() {
        BatteryIconColumn batteryIconColumn = new BatteryIconColumn(context, fontAwesome, resources.getDimension(R.dimen.icon_size), grid.getTextColor());
        batteryIconColumn.setHorizontalMargin(resources.getDimension(R.dimen.icon_margin));
        grid.getRow("batteryRow").putColumn("battery_icon", batteryIconColumn);
        grid.getRow("batteryRow").setMarginBottom(resources.getDimension(R.dimen.row_margin_bottom));

        BatteryLevelColumn batteryLevelColumn = new BatteryLevelColumn(context, defaultTypeface, resources.getDimension(R.dimen.battery_text_size), grid.getTextColor());
        grid.getRow("batteryRow").putColumn("battery", batteryLevelColumn);
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
        for (Map.Entry<String, Column> columnEntry : grid.getRow("sensorsRow").getAllColumns().entrySet()) {
            Column column = columnEntry.getValue();
            column.runTasks();
        }
        setGridColors();
    }


    public void setTimeFormat24(Boolean timeFormat24) {
        TimeColumn timeColumn = (TimeColumn) grid.getRow("timeRow").getColumn("time");
        timeColumn.setTimeFormat24(timeFormat24);

        if (timeFormat24) {
            grid.getRow("timeRow").removeColumn("amPm");
        } else {
            AmPmColumn amPmColumn = new AmPmColumn(context, defaultTypeface, resources.getDimension(R.dimen.time_am_pm_size), grid.getTextColor());
            grid.getRow("timeRow").putColumn("amPm", amPmColumn);
        }
    }

    public void setShowDateNamesFormat(Boolean showDateNamesFormat) {
        DateColumn dateColumn = (DateColumn) grid.getRow("dateRow").getColumn("date");
        dateColumn.setShowDateNamesFormat(showDateNamesFormat);
    }

    public void shouldInterlace(Boolean shouldInterlace) {
        this.interlace = shouldInterlace;
    }

    public void setInvertBlackAndWhite(Boolean invertBlackAndWhite) {
        this.invertBlackAndWhite = invertBlackAndWhite;
        setGridColors();
    }

    public void showGoogleFitSteps(Boolean showGoogleFitSteps){

    }

    private void setGridColors() {
        if (invertBlackAndWhite) {
            if (dayNightMode) {
                grid.setBackgroundColor(SunriseSunsetHelper.isDay() ? Color.WHITE : Color.BLACK);
                grid.setTextColor(SunriseSunsetHelper.isDay() ? Color.BLACK : Color.WHITE);
            } else {
                grid.setBackgroundColor(Color.WHITE);
                grid.setTextColor(Color.BLACK);
            }
        } else {
            if (dayNightMode) {
                grid.setBackgroundColor(SunriseSunsetHelper.isDay() ? Color.BLACK : Color.WHITE);
                grid.setTextColor(SunriseSunsetHelper.isDay() ? Color.WHITE : Color.BLACK);
            } else {
                grid.setBackgroundColor(Color.BLACK);
                grid.setTextColor(Color.WHITE);
            }
        }

    }

    public void setIsRound(boolean round) {
        isRound = round;
    }

    public void setChinSize(Integer chinSize) {
        this.chinSize = chinSize;
    }

    public void setDayNightMode(boolean dayNightMode) {
        this.dayNightMode = dayNightMode;
    }
}
