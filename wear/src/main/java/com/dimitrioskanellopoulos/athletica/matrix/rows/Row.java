package com.dimitrioskanellopoulos.athletica.matrix.rows;

import com.dimitrioskanellopoulos.athletica.matrix.columns.Column;

import java.util.LinkedHashMap;

public class Row implements RowInterface {
    private LinkedHashMap<String, Column> columns;

    @Override
    public void addColumn(String name, Column column) {
        columns.put(name, column);
    }

    @Override
    public Column getColumn(String name) {
        return columns.get(name);
    }

    @Override
    public Column[] getAllColumns() {
        return (Column[]) columns.values().toArray();
    }
}
