package com.dimitrioskanellopoulos.athletica.grid;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.dimitrioskanellopoulos.athletica.BuildConfig;
import com.dimitrioskanellopoulos.athletica.grid.columns.Column;
import com.dimitrioskanellopoulos.athletica.grid.rows.Row;

import java.util.Map;
import java.util.TreeMap;

public class GridRenderer {
    private static final String TAG = "GridRenderer";

    /**
     * @todo document more and make it faster
     */
    public static void renderGrid(Canvas canvas, Rect bounds, Grid grid, Float topMargin, Float bottomMargin, Boolean twoColorBackground) {

        drawBackground(canvas, bounds, grid.getBackgroundColor());

        // Get all the rows
        TreeMap<String, Row> rows = grid.getAllRows();

        Float totalHeight = bounds.height() - topMargin - bottomMargin;
        Float rowHeight = ((totalHeight + totalHeight / rows.size() * 0.5f)) / rows.size();
        Float startingOffsetY = topMargin - rowHeight * 0.5f;

        if (BuildConfig.DEBUG) {
            // Do the setBaseline
            Paint bluePaint = new Paint();
            bluePaint.setColor(Color.BLUE);
            canvas.drawLine(bounds.left, bounds.exactCenterY(), bounds.right, bounds.exactCenterY(), bluePaint);
            // Do the bottom
            canvas.drawLine(bounds.left, bounds.exactCenterY() + totalHeight - 1.0f, bounds.right, bounds.exactCenterY() + totalHeight - 1.0f, bluePaint);
            // Do the middle
            canvas.drawLine(bounds.exactCenterY(), bounds.top, bounds.exactCenterY(), bounds.bottom, bluePaint);
        }

        int rowCount = 0;
        for (Map.Entry<String, Row> rowEntry : rows.entrySet()) {
            Row row = rowEntry.getValue();
            if (BuildConfig.DEBUG) {
                Paint greenPaint = new Paint();
                greenPaint.setColor(Color.GREEN);
                canvas.drawLine(bounds.left, startingOffsetY + rowCount * rowHeight, bounds.right, startingOffsetY + rowCount * rowHeight, greenPaint);
            }

            float rowOffsetY = startingOffsetY + rowCount * rowHeight;
            float totalTextWidth = 0f;
            float maxTextHeight = 0f;

            if (twoColorBackground && rowCount == 0){
                Rect rect = new Rect();
                rect.set(bounds.left, bounds.top, bounds.right, Math.round(rowOffsetY + rowHeight*0.75f));
                drawBackground(canvas, rect, grid.getBackgroundColor() == Color.BLACK ? Color.WHITE : Color.BLACK);
            }

            for (Map.Entry<String, Column> columnEntry : row.getAllColumns().entrySet()) {
                Column column = columnEntry.getValue();
                totalTextWidth += column.getWidth() + column.getHorizontalMargin();
                if (column.getHeight() > maxTextHeight) {
                    maxTextHeight = column.getHeight();
                }
            }

            float previousColumnOffsetY = rowHeight;
            float cursor = bounds.exactCenterX() - totalTextWidth * 0.5f;
            for (Map.Entry<String, Column> columnEntry : row.getAllColumns().entrySet()) {
                Column column = columnEntry.getValue();
                if (BuildConfig.DEBUG) {
                    Paint greenPaint = new Paint();
                    greenPaint.setColor(Color.GREEN);
                    Paint bluePaint = new Paint();
                    bluePaint.setColor(Color.BLUE);
                    canvas.drawLine(cursor, startingOffsetY + rowCount * rowHeight, cursor, (startingOffsetY + rowCount * rowHeight) + rowHeight, greenPaint);
                    canvas.drawLine(cursor + column.getHorizontalMargin() + column.getWidth(), startingOffsetY + rowCount * rowHeight, cursor + column.getHorizontalMargin() + column.getWidth(), (startingOffsetY + rowCount * rowHeight) + rowHeight, bluePaint);
                }


                Float columnOffsetY;
                switch (column.getBaseline()) {
                    case Column.BASELINE_TOP:
                        columnOffsetY = 0.0f;
                        break;
                    case Column.BASELINE_MIDDLE:
                        columnOffsetY = rowHeight / 2 + column.getHeight() / 2;
                        break;
                    case Column.BASELINE_ABSOLUTE_CENTER:
                        columnOffsetY = rowHeight / 2;
                        break;
                    case Column.BASELINE_PREVIOUS:
                        columnOffsetY = previousColumnOffsetY;
                        break;
                    case Column.BASELINE_BOTTOM:
                        columnOffsetY = rowOffsetY;
                        break;
                    default:
                        columnOffsetY = column.getHeight();
                        break;
                }

                // If it's bigger pretend it fits so we can get the middle position (center) easily
                if (columnOffsetY > rowHeight) {
                    columnOffsetY = rowHeight;
                }

                // Draw the column
                if (twoColorBackground && rowCount == 0){
                    column.getPaint().setColor(column.getPaint().getColor() == Color.BLACK ? Color.WHITE : Color.BLACK);
                    canvas.drawText(column.getText(), cursor, rowOffsetY + columnOffsetY, column.getPaint()); // check if it needs per column height
                    column.getPaint().setColor(column.getPaint().getColor() == Color.BLACK ? Color.WHITE : Color.BLACK);
                }else {
                    canvas.drawText(column.getText(), cursor, rowOffsetY + columnOffsetY, column.getPaint()); // check if it needs per column height
                }
                // canvas.drawText(column.getText(), cursor, rowOffsetY + rowHeight, column.getPaint()); // check if it needs per column height
                cursor += column.getWidth() + column.getHorizontalMargin();

                previousColumnOffsetY = columnOffsetY;

                //Log.d(TAG, "Drew column cursor " + cursor);
            }
            rowCount++;
            //Log.d(TAG, "Drew row " + rowCount + " offsetY " + rowOffsetY);
        }
        if (BuildConfig.DEBUG) {
            Paint greenPaint = new Paint();
            greenPaint.setColor(Color.GREEN);
            canvas.drawLine(bounds.left, startingOffsetY + rowCount * rowHeight, bounds.right, startingOffsetY + rowCount * rowHeight, greenPaint);
        }
    }

    public static void drawBackground(Canvas canvas, Rect bounds, Integer color) {
            Paint backgroundPaint = new Paint();
            backgroundPaint.setColor(color);
        canvas.drawRect(0, 0, bounds.width(), bounds.height(), backgroundPaint);
    }

    public static void drawTicks(Canvas canvas, Rect bounds, Integer color, Float strokeWidth) {
        Paint tickPaing = new Paint();
        tickPaing.setColor(color);
        tickPaing.setStrokeWidth(strokeWidth);

        float innerTickRadius = bounds.exactCenterX() - 20;
        float outerTickRadius = bounds.exactCenterX();
        for (int tickIndex = 0; tickIndex < 12; tickIndex++) {
            float tickRot = (float) (tickIndex * Math.PI * 2 / 12);
            float innerX = (float) Math.sin(tickRot) * innerTickRadius;
            float innerY = (float) -Math.cos(tickRot) * innerTickRadius;
            float outerX = (float) Math.sin(tickRot) * outerTickRadius;
            float outerY = (float) -Math.cos(tickRot) * outerTickRadius;
            canvas.drawLine(bounds.exactCenterX() + innerX, bounds.exactCenterX() + innerY,
                    bounds.exactCenterX() + outerX, bounds.exactCenterX() + outerY, tickPaing);
        }
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
