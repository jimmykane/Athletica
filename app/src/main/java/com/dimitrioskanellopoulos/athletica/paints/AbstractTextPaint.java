package com.dimitrioskanellopoulos.athletica.paints;

import android.graphics.Paint;

import com.dimitrioskanellopoulos.athletica.TextPaintInterface;

/**
 * A AbstractTextPaint is a normal paint that has just an extra placeholder for text
 */
public abstract class AbstractTextPaint extends Paint implements TextPaintInterface {
    protected String text = "";
}
