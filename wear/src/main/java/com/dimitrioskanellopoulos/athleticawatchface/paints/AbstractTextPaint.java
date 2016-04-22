package com.dimitrioskanellopoulos.athleticawatchface.paints;

import android.graphics.Paint;

import com.dimitrioskanellopoulos.athleticawatchface.paints.interfaces.TextPaintInterface;

public abstract class AbstractTextPaint extends Paint implements TextPaintInterface {
    protected String text;
}
