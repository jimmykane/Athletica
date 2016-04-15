package com.dimitrioskanellopoulos.athletica;

import android.graphics.Paint;

/**
 * A AbstractTextPaint is a normal paint that has just an extra placeholder for text
 */
abstract class AbstractTextPaint extends Paint implements TextPaint{
    protected String text = "";
}
