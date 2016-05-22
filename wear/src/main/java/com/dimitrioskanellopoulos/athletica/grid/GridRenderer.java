package com.dimitrioskanellopoulos.athletica.grid;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.util.TypedValue;

import com.dimitrioskanellopoulos.athletica.BuildConfig;
import com.dimitrioskanellopoulos.athletica.grid.columns.Column;
import com.dimitrioskanellopoulos.athletica.grid.rows.Row;

public class GridRenderer {
    private static final String TAG = "GridRenderer";

    /**
     * @todo document more and make it faster
     */
    public static void drawRows(Canvas canvas, Rect bounds, Row[] rows, Integer bottomMargin, Boolean centerOnY) {

        float totalHeight = bounds.height() - bottomMargin;
        float startingOffsetY = 0.0f;
        float rowHeight = totalHeight / rows.length;
        if (centerOnY) {
            totalHeight = bounds.height() / 2.0f - bottomMargin;
            rowHeight = ((totalHeight + totalHeight / rows.length / 2.0f)) / rows.length;
            startingOffsetY = bounds.exactCenterY() - rowHeight / 2.0f;
        }

        int rowCount = 0;
        for (Row row : rows) {
            if (BuildConfig.DEBUG) {
                Paint greenPaint = new Paint();
                greenPaint.setColor(Color.GREEN);
                Paint bluePaint = new Paint();
                bluePaint.setColor(Color.BLUE);
                canvas.drawLine(bounds.left, startingOffsetY + rowCount * rowHeight, bounds.right, startingOffsetY + rowCount * rowHeight, greenPaint);
                canvas.drawLine(bounds.left, (startingOffsetY + rowCount * rowHeight) + rowHeight, bounds.right, (startingOffsetY + rowCount * rowHeight) + rowHeight, bluePaint);
            }

            float yOffset = startingOffsetY + rowCount * rowHeight;
            Float totalTextWidth = 0f;
            for (Column column : row.getAllColumns()) {
                totalTextWidth += column.getWidth() + column.getHorizontalMargin();
            }

            float cursor = bounds.exactCenterX() - totalTextWidth * 0.5f;
            for (Column column : row.getAllColumns()) {
                if (BuildConfig.DEBUG) {
                    Paint greenPaint = new Paint();
                    greenPaint.setColor(Color.GREEN);
                    Paint bluePaint = new Paint();
                    bluePaint.setColor(Color.BLUE);
                    canvas.drawLine(cursor, startingOffsetY + rowCount * rowHeight, cursor, (startingOffsetY + rowCount * rowHeight) + rowHeight, greenPaint);
                    canvas.drawLine(cursor + column.getHorizontalMargin() + column.getWidth(), startingOffsetY + rowCount * rowHeight, cursor + column.getHorizontalMargin() + column.getWidth(), (startingOffsetY + rowCount * rowHeight) + rowHeight, bluePaint);
                }
                // Draw the column
                canvas.drawText(column.getText(), cursor, yOffset + rowHeight, column.getPaint()); // check if it needs per column height
                cursor += column.getWidth() + column.getHorizontalMargin();
                Log.d(TAG, "Drew column cursor " + cursor);
            }
            rowCount++;
            Log.d(TAG, "Drew row " + rowCount + " offsetY " + yOffset);
        }
    }

    public static void drawBackground(Canvas canvas, Rect bounds, Integer color) {
        Paint backgroundPaint = new Paint();
        backgroundPaint.setColor(color);
        canvas.drawRect(0, 0, bounds.width(), bounds.height(), backgroundPaint);
    }

    public static void interlaceCanvas(Canvas canvas, Rect bounds, Integer color, Integer alpha) {
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
