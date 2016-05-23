package com.dimitrioskanellopoulos.athletica.grid;

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

}
