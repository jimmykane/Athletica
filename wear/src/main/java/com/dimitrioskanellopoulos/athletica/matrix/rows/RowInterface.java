package com.dimitrioskanellopoulos.athletica.matrix.rows;

import com.dimitrioskanellopoulos.athletica.matrix.columns.Column;

public interface RowInterface {
    void addColumn(String name, Column column);
    void removeColumn(String name);
    void setVerticalMargin(Float verticalMargin);

    Column getColumn(String name);
    Column[] getAllColumns();
}