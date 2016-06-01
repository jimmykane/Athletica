package com.dimitrioskanellopoulos.athletica.grid;

import android.graphics.Color;

import com.dimitrioskanellopoulos.athletica.grid.columns.Column;
import com.dimitrioskanellopoulos.athletica.grid.rows.Row;

import java.util.LinkedHashMap;
import java.util.Map;

public class Grid {
    private static final String TAG = "Grid";
    private final LinkedHashMap<String, Row> rows = new LinkedHashMap<>();
    private Integer backgroundColor = Color.BLACK;
    private Integer textColor = Color.WHITE;

    public void putRow(String rowName, Row row) {
        rows.put(rowName, row);
    }

    public void removeRow(String rowName) {
        if (rows.containsKey(rowName)){
            rows.get(rowName).removeAllColumns();
        }
        rows.remove(rowName);
    }

    public Row getRow(String rowName) {
        return rows.get(rowName);
    }

    public LinkedHashMap<String, Row> getAllRows() {
        return rows;
    }

    /**
     * Toggles the ambient or not mode for all the rows
     */
    public void setInAmbientMode(boolean inAmbientMode) {
        for (Map.Entry<String, Row> rowEntry : getAllRows().entrySet()) {
            Row row = rowEntry.getValue();
            for (Map.Entry<String, Column> columnEntry : row.getAllColumns().entrySet()) {
                Column column = columnEntry.getValue();
                column.setAmbientMode(inAmbientMode);
            }
        }
    }

    /**
     * Toggles the visible or not mode for all the columns
     */
    public void setIsVisible(boolean isVisible) {
        for (Map.Entry<String, Row> rowEntry : getAllRows().entrySet()) {
            Row row = rowEntry.getValue();
            for (Map.Entry<String, Column> columnEntry : row.getAllColumns().entrySet()) {
                Column column = columnEntry.getValue();
                column.setIsVisible(isVisible);
            }
        }
    }

    public Integer getTextColor() {
        return textColor;
    }

    public void setTextColor(Integer textColor) {
        this.textColor = textColor;
        for (Map.Entry<String, Row> rowEntry : getAllRows().entrySet()) {
            Row row = rowEntry.getValue();
            for (Map.Entry<String, Column> columnEntry : row.getAllColumns().entrySet()) {
                Column column = columnEntry.getValue();
                column.setTextDefaultColor(textColor);
            }
        }
    }

    public Integer getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Integer backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void invertColors() {
        setBackgroundColor(getBackgroundColor() == Color.BLACK ? Color.WHITE : Color.BLACK);
        setTextColor(getTextColor() == Color.WHITE ? Color.BLACK : Color.WHITE);
    }
}
