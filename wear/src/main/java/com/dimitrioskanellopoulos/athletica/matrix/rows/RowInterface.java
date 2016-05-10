package com.dimitrioskanellopoulos.athletica.matrix.rows;

import com.dimitrioskanellopoulos.athletica.matrix.columns.Column;

public interface RowInterface {
    void addColumn(String name, Column column);
    Column getColumn(String name);
    Column[] getAllColumns();
}
