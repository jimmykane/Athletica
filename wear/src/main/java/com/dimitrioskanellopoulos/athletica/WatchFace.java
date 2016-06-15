package com.dimitrioskanellopoulos.athletica;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;

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

class WatchFace {
    private static final String TAG = "Watchface";

    private static final Grid grid = new Grid();

    private final Context context;
    private final Resources resources;

    private final Typeface fontAwesome;
    private final Typeface materialIconsFont;
    private Typeface defaultTypeface;
    private boolean ambientMode = false;
    private boolean shouldAntialiasInAmbientMode = false;
    private boolean isVisible = false;
    private boolean interlace = true;
    private boolean dayNightMode = false;
    private Boolean invertBlackAndWhite = false;
    private boolean twoColorBackground = false;

    private int chinSize;
    private boolean isRound = false;

    /**
     * The WatchFace. Everything the user sees. No extra init or data manipulation
     */
    WatchFace(Context context) {

        this.context = context;
        resources = context.getResources();

        // Default typeface
        defaultTypeface = Typeface.SANS_SERIF;

        fontAwesome = Typeface.createFromAsset(context.getAssets(), "fonts/fontawesome-webfont.ttf");
        materialIconsFont = Typeface.createFromAsset(context.getAssets(), "fonts/MaterialIcons-Regular.ttf");
    }

    void draw(Canvas canvas, Rect bounds) {
        GridRenderer.renderGrid(canvas, bounds, grid, bounds.height() / 2.0f, chinSize + resources.getDimension(R.dimen.face_bottom_margin));
        if (interlace) {
            GridRenderer.interlaceCanvas(canvas, bounds, Color.BLACK, 100);
        }
    }

    void addRowForTime() {
        Row timeRow = new Row();
        TimeColumn timeColumn = new TimeColumn(context, defaultTypeface, resources.getDimension(R.dimen.time_size), grid.getTextColor(), isVisible, ambientMode);
        timeColumn.setBaseline(Column.BASELINE_ABSOLUTE_CENTER);
        timeColumn.shouldAntialiasInAmbientMode(shouldAntialiasInAmbientMode);
        timeRow.putColumn("timeColumn", timeColumn);
        grid.putRow("1_timeRow", timeRow);
    }

    void addRowForDate() {
        Row dateRow = new Row();
        DateColumn dateColumn = new DateColumn(context, defaultTypeface, resources.getDimension(R.dimen.date_size), grid.getTextColor(), isVisible, ambientMode);
        dateColumn.shouldAntialiasInAmbientMode(shouldAntialiasInAmbientMode);
        dateRow.putColumn("dateColumn", dateColumn);
        grid.putRow("2_dateRow", dateRow);
    }

    Boolean hasSensorColumn(Integer sensorType) {
        return grid.getRow("5_sensorsRow") != null && grid.getRow("5_sensorsRow").getColumn(sensorType.toString()) != null;
    }

    void addSensorColumn(Integer sensorType) {
        // Check if row is there and else create it
        Row sensorsRow = grid.getRow("5_sensorsRow");
        if (sensorsRow == null) {
            sensorsRow = new Row();
            grid.putRow("5_sensorsRow", sensorsRow);
        }
        // Add icon
        if (sensorsRow.getAllColumns().size() >= 3) {
            sensorsRow.getAllColumns().values().toArray(new Column[0])[sensorsRow.getAllColumns().size() - 1].setHorizontalMargin(resources.getDimension(R.dimen.column_margin));
        }
        Column sensorIconColumn = ColumnFactory.getIconColumnForSensorType(context, sensorType, fontAwesome, resources.getDimension(R.dimen.icon_size), grid.getTextColor(), isVisible, ambientMode);
        sensorIconColumn.setHorizontalMargin(resources.getDimension(R.dimen.icon_margin));
        sensorIconColumn.shouldAntialiasInAmbientMode(shouldAntialiasInAmbientMode);
        sensorsRow.putColumn(sensorType.toString() + "Icon", sensorIconColumn);

        // Add column depending on emu
        Column sensorColumn;
        if (EmulatorHelper.isEmulator()) {
            sensorColumn = new Column(context, defaultTypeface, resources.getDimension(R.dimen.text_size), grid.getTextColor(), isVisible, ambientMode);
            sensorColumn.setText("21");
        } else {
            sensorColumn = ColumnFactory.getColumnForSensorType(context, sensorType, defaultTypeface, resources.getDimension(R.dimen.text_size), grid.getTextColor(), isVisible, ambientMode);
        }

        sensorColumn.setIsVisible(isVisible);
        sensorColumn.setHorizontalMargin(resources.getDimension(R.dimen.units_margin));
        sensorColumn.shouldAntialiasInAmbientMode(shouldAntialiasInAmbientMode);
        sensorsRow.putColumn(sensorType.toString(), sensorColumn);

        // Add units
        Column sensorUnitsColumn = ColumnFactory.getUnitsColumnForSensorType(context, sensorType, defaultTypeface, resources.getDimension(R.dimen.units_size), grid.getTextColor(), isVisible, ambientMode);
        sensorUnitsColumn.setBaseline(Column.BASELINE_PREVIOUS);
        sensorUnitsColumn.shouldAntialiasInAmbientMode(shouldAntialiasInAmbientMode);
        sensorsRow.putColumn(sensorType.toString() + "Units", sensorUnitsColumn);
    }

    void removeSensorsRow() {
        grid.removeRow("5_sensorsRow");
    }

    void addRowForBattery() {
        Row batteryRow = new Row();
        //Icon
        BatteryIconColumn batteryIconColumn = new BatteryIconColumn(context, fontAwesome, resources.getDimension(R.dimen.battery_icon_size), grid.getTextColor(), isVisible, ambientMode);
        batteryIconColumn.setHorizontalMargin(resources.getDimension(R.dimen.icon_margin));
        batteryIconColumn.shouldAntialiasInAmbientMode(shouldAntialiasInAmbientMode);
        batteryRow.putColumn("batteryIconColumn", batteryIconColumn);

        // Column
        BatteryLevelColumn batteryLevelColumn = new BatteryLevelColumn(context, defaultTypeface, resources.getDimension(R.dimen.battery_text_size), grid.getTextColor(), isVisible, ambientMode);
        batteryLevelColumn.shouldAntialiasInAmbientMode(shouldAntialiasInAmbientMode);
        batteryLevelColumn.setBaseline(Column.BASELINE_PREVIOUS);
        batteryRow.putColumn("batteryLevelColumn", batteryLevelColumn);

        grid.putRow("6_batteryRow", batteryRow);
    }

    /**
     * Toggles the ambient or not mode for all the paints
     */
    void setInAmbientMode(boolean inAmbientMode) {
        ambientMode = inAmbientMode;
        grid.setInAmbientMode(inAmbientMode);
    }

    /**
     * Toggles the visible or not mode for all the columns
     */
    void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
        grid.setIsVisible(isVisible);
    }

    /**
     * Run's tasks for every column
     */
    void runTasks() {
        grid.runTasks();
        setGridColors();
    }


    void setTimeFormat24(Boolean timeFormat24) {
        TimeColumn timeColumn = (TimeColumn) grid.getRow("1_timeRow").getColumn("timeColumn");
        timeColumn.setTimeFormat24(timeFormat24);

        if (timeFormat24) {
            grid.getRow("1_timeRow").removeColumn("amPmColumn");
        } else {
            AmPmColumn amPmColumn = new AmPmColumn(context, defaultTypeface, resources.getDimension(R.dimen.time_am_pm_size), grid.getTextColor(), isVisible, ambientMode);
            amPmColumn.setBaseline(timeColumn.getBaseline());
            amPmColumn.shouldAntialiasInAmbientMode(shouldAntialiasInAmbientMode);
            grid.getRow("1_timeRow").putColumn("amPmColumn", amPmColumn);
        }
    }

    void setShowDateNamesFormat(Boolean showDateNamesFormat) {
        DateColumn dateColumn = (DateColumn) grid.getRow("2_dateRow").getColumn("dateColumn");
        dateColumn.setShowDateNamesFormat(showDateNamesFormat);
    }

    void shouldInterlace(Boolean shouldInterlace) {
        this.interlace = shouldInterlace;
    }

    void setInvertBlackAndWhite(Boolean invertBlackAndWhite) {
        this.invertBlackAndWhite = invertBlackAndWhite;
        setGridColors();
    }

    void showGoogleFitSteps(Boolean showGoogleFitSteps) {
        Log.d(TAG, "showGoogleFitSteps " + showGoogleFitSteps.toString());
        if (showGoogleFitSteps) {
            if (grid.getRow("4_googleFitRow") == null) {
                // Icon
                Column googleFitStepsIconColumn = new Column(context, materialIconsFont, resources.getDimension(R.dimen.icon_size), grid.getTextColor(), isVisible, ambientMode);
                //googleFitStepsIconColumn.setHorizontalMargin(resources.getDimension(R.dimen.icon_margin)); // No margin needed for these icons
                googleFitStepsIconColumn.setText(resources.getString(R.string.icon_google_fit_steps));
                googleFitStepsIconColumn.shouldAntialiasInAmbientMode(shouldAntialiasInAmbientMode);
                Row googleFitRow = new Row();
                googleFitRow.putColumn("googleFitStepsColumnIcon", googleFitStepsIconColumn);

                // Steps
                GoogleFitStepsColumn googleFitStepsColumn = new GoogleFitStepsColumn(context, defaultTypeface, resources.getDimension(R.dimen.text_size), grid.getTextColor(), isVisible, ambientMode);
                googleFitStepsColumn.shouldAntialiasInAmbientMode(shouldAntialiasInAmbientMode);
                googleFitRow.putColumn("googleFitStepsColumn", googleFitStepsColumn);
                googleFitStepsColumn.setHorizontalMargin(resources.getDimension(R.dimen.units_margin));

                // Steps
                Column googleFitStepsUnitsColumn = new Column(context, defaultTypeface, resources.getDimension(R.dimen.units_size), grid.getTextColor(), isVisible, ambientMode);
                googleFitStepsUnitsColumn.setText("steps");
                googleFitStepsUnitsColumn.setBaseline(Column.BASELINE_PREVIOUS);
                googleFitStepsUnitsColumn.shouldAntialiasInAmbientMode(shouldAntialiasInAmbientMode);
                googleFitRow.putColumn("googleFitStepsUnitsColumn", googleFitStepsUnitsColumn);
                grid.putRow("4_googleFitRow", googleFitRow);
            }
        } else {
            grid.removeRow("4_googleFitRow");
        }
    }


    void showSunriseSunsetTimes(Boolean showSunriseSunsetTimes) {
        Log.d(TAG, "showSunriseSunsetTimes " + showSunriseSunsetTimes.toString());
        if (showSunriseSunsetTimes) {
            if (grid.getRow("3_sunriseSunsetRow") == null) {
                Row sunriseSunsetRow = new Row();
                // Icon
                Column sunriseIconColumn = new Column(context, materialIconsFont, resources.getDimension(R.dimen.icon_size), grid.getTextColor(), isVisible, ambientMode);
                sunriseIconColumn.setText(resources.getString(R.string.icon_sunrise));
                sunriseIconColumn.setHorizontalMargin(resources.getDimension(R.dimen.icon_margin));
                sunriseIconColumn.shouldAntialiasInAmbientMode(shouldAntialiasInAmbientMode);
                sunriseSunsetRow.putColumn("sunriseIconColumn", sunriseIconColumn);
                // Column
                SunriseColumn sunriseColumn = new SunriseColumn(context, defaultTypeface, resources.getDimension(R.dimen.text_size), grid.getTextColor(), isVisible, ambientMode);
                sunriseColumn.setHorizontalMargin(resources.getDimension(R.dimen.column_margin));
                sunriseColumn.shouldAntialiasInAmbientMode(shouldAntialiasInAmbientMode);
                sunriseSunsetRow.putColumn("sunriseColumn", sunriseColumn);

                // Icon
                Column sunsetIconColumn = new Column(context, fontAwesome, resources.getDimension(R.dimen.icon_size), grid.getTextColor(), isVisible, ambientMode);
                sunsetIconColumn.setHorizontalMargin(resources.getDimension(R.dimen.icon_margin));
                sunsetIconColumn.setText(resources.getString(R.string.icon_sunset));
                sunsetIconColumn.shouldAntialiasInAmbientMode(shouldAntialiasInAmbientMode);
                sunriseSunsetRow.putColumn("sunsetIconColumn", sunsetIconColumn);

                // Column
                SunsetColumn sunsetColumn = new SunsetColumn(context, defaultTypeface, resources.getDimension(R.dimen.text_size), grid.getTextColor(), isVisible, ambientMode);
                sunsetColumn.shouldAntialiasInAmbientMode(shouldAntialiasInAmbientMode);
                sunriseSunsetRow.putColumn("sunsetColumn", sunsetColumn);

                grid.putRow("3_sunriseSunsetRow", sunriseSunsetRow);
            }
        } else {
            grid.removeRow("3_sunriseSunsetRow");
        }
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

    void setIsRound(boolean round) {
        isRound = round;
    }

    void setChinSize(Integer chinSize) {
        this.chinSize = chinSize;
    }

    void setDayNightMode(boolean dayNightMode) {
        this.dayNightMode = dayNightMode;
    }

    void setTwoColorBackground(boolean twoColorBackground) {
        this.twoColorBackground = twoColorBackground;
    }

    public void shouldAntialiasInAmbientMode(Boolean shouldAntialiasInAmbientMode) {
        grid.shouldAntialiasInAmbientMode(shouldAntialiasInAmbientMode);
    }
}
