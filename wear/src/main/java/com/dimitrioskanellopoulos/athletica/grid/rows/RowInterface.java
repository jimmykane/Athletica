package com.dimitrioskanellopoulos.athletica.grid.rows;

import com.dimitrioskanellopoulos.athletica.grid.columns.Column;

import java.util.LinkedHashMap;

public interface RowInterface {
    void putColumn(String name, Column column);

    void removeColumn(String name);

    Column getColumn(String name);

    LinkedHashMap<String, Column> getAllColumns();

    Float getPaddingBottom();

    void setPaddingBottom(Float verticalMargin);
}
