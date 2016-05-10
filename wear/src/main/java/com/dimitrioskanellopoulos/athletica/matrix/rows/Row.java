package com.dimitrioskanellopoulos.athletica.matrix.rows;

import com.dimitrioskanellopoulos.athletica.matrix.columns.Column;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Row implements RowInterface {
    private LinkedHashMap<String, Column> columns = new LinkedHashMap<>();
    private Float verticalMargin = 0.0f;

    @Override
    public void addColumn(String name, Column column) {
        columns.put(name, column);
    }

    @Override
    public void removeColumn(String name) {
        columns.remove(name);
    }

    @Override
    public void setVerticalMargin(Float verticalMargin) {
        this.verticalMargin = verticalMargin;
    }

    @Override
    public Column getColumn(String name) {
        return columns.get(name);
    }

    @Override
    public Column[] getAllColumns() {
        return columns.values().toArray(new Column[0]);
    }

    @Override
    public Float getVerticalMargin() {
         return verticalMargin;
    }
}
