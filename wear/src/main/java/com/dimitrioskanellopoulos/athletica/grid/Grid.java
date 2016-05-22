package com.dimitrioskanellopoulos.athletica.grid;

import com.dimitrioskanellopoulos.athletica.grid.columns.Column;
import com.dimitrioskanellopoulos.athletica.grid.rows.Row;

import java.util.LinkedHashMap;

public class Grid {

    private static final LinkedHashMap<String, Row> rows = new LinkedHashMap<>();

    public void putRow(String rowName, Row row){
        rows.put(rowName, row);
    }

    public void removeRow(String rowName){
        rows.remove(rowName);
    }

    public Row getRow(String rowName){
        return rows.get(rowName);
    }

    public Row[] getAllRows() {
        return rows.values().toArray(new Row[0]);
    }

    public void putColumn(String rowName, String columnName, Column column){
        rows.get(rowName).putColumn(columnName, column);
    }

    public void removeColumn(String rowName, String columnName){
        rows.get(rowName).removeColumn(columnName);
    }

    public Column getColumn(String rowName, String columnName){
        return rows.get(rowName).getColumn(columnName);
    }
}
