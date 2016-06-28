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
    public static void renderGrid(Canvas canvas, Rect bounds, Grid grid, Float topMargin, Float bottomMargin) {

        drawBackground(canvas, bounds, grid.getBackgroundColor());

        // Get all the rows
        TreeMap<String, Row> rows = grid.getAllRows();

        Float totalHeight = bounds.height() - topMargin - bottomMargin;
        Float rowHeight;

        // Check for notif
        Row notificationsRow = rows.get("7_notificationRow");
        if (notificationsRow != null){
            rowHeight = ((totalHeight + totalHeight / (rows.size() -1))) / (rows.size()-1);
        }else{
            rowHeight = ((totalHeight + totalHeight / rows.size())) / rows.size();
        }
        Float startingOffsetY = topMargin - rowHeight;
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
            float rowOffsetY =  startingOffsetY + rowCount * rowHeight;
            if (BuildConfig.DEBUG) {
                Paint greenPaint = new Paint();
                greenPaint.setColor(Color.GREEN);
                canvas.drawLine(bounds.left,rowOffsetY, bounds.right, rowOffsetY, greenPaint);
            }

            drawRow(canvas, bounds, row, rowOffsetY, rowHeight);

            rowCount++;
            //Log.d(TAG, "Drew row " + rowCount + " offsetY " + rowOffsetY);
        }

        if (BuildConfig.DEBUG) {
            Paint greenPaint = new Paint();
            greenPaint.setColor(Color.GREEN);
            canvas.drawLine(bounds.left, startingOffsetY + rowCount * rowHeight, bounds.right, startingOffsetY + rowCount * rowHeight, greenPaint);
        }

        // Here it should draw the notifications
    }

    private static void drawBackground(Canvas canvas, Rect bounds, Integer color) {
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

    private static void drawRow(Canvas canvas, Rect bounds, Row row, Float startingOffsetY, Float rowHeight){
        if (BuildConfig.DEBUG) {
            Paint greenPaint = new Paint();
            greenPaint.setColor(Color.GREEN);
            canvas.drawLine(bounds.left, startingOffsetY, bounds.right, startingOffsetY, greenPaint);
        }

        float rowOffsetY =startingOffsetY;

        float previousColumnOffsetY = rowHeight;
        float cursor = bounds.exactCenterX() - row.getColumnsTotalWidth() * 0.5f;
        for (Map.Entry<String, Column> columnEntry : row.getAllColumns().entrySet()) {
            Column column = columnEntry.getValue();
            if (BuildConfig.DEBUG) {
                Paint greenPaint = new Paint();
                greenPaint.setColor(Color.GREEN);
                Paint bluePaint = new Paint();
                bluePaint.setColor(Color.BLUE);
                canvas.drawLine(cursor,startingOffsetY, cursor, startingOffsetY + rowHeight, greenPaint);
                canvas.drawLine(cursor + column.getHorizontalMargin() + column.getWidth(),startingOffsetY, cursor + column.getHorizontalMargin() + column.getWidth(), startingOffsetY + rowHeight, bluePaint);
            }


            Float columnOffsetY;
            switch (column.getBaseline()) {
                case Column.BASELINE_TOP:
                    columnOffsetY = row.getColumnsMaxHeight();
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
                case Column.BASELINE_OVER_THE_TOP:
                    columnOffsetY = 0f - column.getHeight();
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
            canvas.drawText(column.getText(), cursor, rowOffsetY + columnOffsetY, column.getPaint()); // check if it needs per column height

            // canvas.drawText(column.getText(), cursor, rowOffsetY + rowHeight, column.getPaint()); // check if it needs per column height
            cursor += column.getWidth() + column.getHorizontalMargin();

            previousColumnOffsetY = columnOffsetY;

            //Log.d(TAG, "Drew column cursor " + cursor);
        }
    }


}
