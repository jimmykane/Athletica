package com.dimitrioskanellopoulos.athletica.grid;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import com.dimitrioskanellopoulos.athletica.grid.columns.Column;
import com.dimitrioskanellopoulos.athletica.grid.rows.Row;

public class GridRenderer {
    private static final String TAG = "GridRenderer";

    /**
     * @todo document more and make it faster
     */
    public static void drawRows(Canvas canvas, Rect bounds, Row[] rows, Integer chinSize) {
        /**
         * We loop over each row:
         * 1. Find the total width of the text so we can center the text on X
         * 2. Find the biggest height of the text so we can offset on Y
         * 3. Take care for special case of last row
         */
        Float yOffset = bounds.exactCenterY();
        int rowCount = 0;
        for (Row row : rows) {
            Float totalTextWidth = 0f;
            Float maxColumnHeight = 0f;
            // Go over the paints (columns of each row)
            int columnCount = 0;
            for (Column column : row.getAllColumns()) {
                // If the height is bigger than the current set it to that
                if (column.getHeight() > maxColumnHeight) {
                    maxColumnHeight = column.getHeight();
                }
                // The total width of the row increases by the Column's text with
                totalTextWidth += column.getWidth() + column.getHorizontalMargin();
                // Remove the horizontal margin if it's the last column
                // if (columnCount >= row.getAllColumns().length) {
                //Log.d(TAG, "Removing last column margin " + column.getHorizontalMargin());
                //totalTextWidth -= column.getHorizontalMargin();
                //}
                // Log.d(TAG, "Row " + rowCount + " Column " + columnCount + " height "+ column.getHeight());
                columnCount++;
            }
            // Add the total height to the offset
            yOffset += row.getVerticalMargin() + maxColumnHeight / 2.0f;
            // Last row change yOffset and put it as low as possible because it's the bottom row
            if (rowCount == rows.length - 1) {
                yOffset = bounds.bottom - chinSize - maxColumnHeight / 2.0f;
            }

            /**
             * All is found and set start drawing
             */
            Float cursor = bounds.exactCenterX() - totalTextWidth / 2.0f;
            columnCount = 0;
            for (Column column : row.getAllColumns()) {
                // Draw the column
                canvas.drawText(column.getText(), cursor, yOffset, column.getPaint()); // check if it needs per column height
                cursor += column.getWidth() + column.getHorizontalMargin();
                columnCount++;
            }
            rowCount++;
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
