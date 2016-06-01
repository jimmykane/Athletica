package com.dimitrioskanellopoulos.athletica.grid.rows;

import com.dimitrioskanellopoulos.athletica.grid.columns.Column;

import java.util.LinkedHashMap;

public class Row implements RowInterface {
    private LinkedHashMap<String, Column> columns = new LinkedHashMap<>();
    private Float marginBottom = 0.0f;

    @Override
    public void putColumn(String name, Column column) {
        columns.put(name, column);
    }

    @Override
    public void removeColumn(String name) {
        if (columns.containsKey(name)) {
            columns.get(name).destroy();
        }
        columns.remove(name);
    }

    @Override
    public Column getColumn(String name) {
        return columns.get(name);
    }

    @Override
    public LinkedHashMap<String, Column> getAllColumns() {
        return columns;
    }

    @Override
    public Float getMarginBottom() {
        return marginBottom;
    }

    public void setMarginBottom(Float marginBottom) {
        this.marginBottom = marginBottom;
    }

    public void removeAllColumns() {
        for (String columnName : columns.keySet()) {
            columns.get(columnName).destroy();
        }
        columns.clear();
    }
}
