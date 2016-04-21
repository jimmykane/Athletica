package com.dimitrioskanellopoulos.athletica.paints;

import android.graphics.Paint;

import com.dimitrioskanellopoulos.athletica.paints.interfaces.TextPaintInterface;

public abstract class AbstractTextPaint extends Paint implements TextPaintInterface {
    protected String text = "";
}
