package com.dimitrioskanellopoulos.athletica.grid;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class GridRenderer {
    public static void interlaceCanvas(Canvas canvas, Rect bounds, Integer color, Integer alpha){
        Paint interlacePaint = new Paint();
        interlacePaint.setColor(color);
        interlacePaint.setAlpha(alpha);
        for (int y = 0; y < bounds.bottom; y += 2) {
            canvas.drawLine(0, y, bounds.right, y, interlacePaint);
        }
        for (int x = 0; x < bounds.right; x += 2) {
            canvas.drawLine(x, 0, x, bounds.bottom, interlacePaint);
        }
    }
}
