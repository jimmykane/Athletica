package com.dimitrioskanellopoulos.athletica.grid;

import com.dimitrioskanellopoulos.athletica.grid.rows.Row;

import java.util.LinkedHashMap;

public class Grid {
    private static final String TAG = "Grid";
    private final LinkedHashMap<String, Row> rows = new LinkedHashMap<>();

    public void putRow(String rowName, Row row){
        rows.put(rowName, row);
    }

//    public void removeRow(String rowName){
//        if (rows.containsKey(rowName)){
//            rows.get(rowName).
//        }
//        rows.remove(rowName);
//    }

    public Row getRow(String rowName){
        return rows.get(rowName);
    }

    public LinkedHashMap<String, Row> getAllRows() {
        return rows;
    }

}
