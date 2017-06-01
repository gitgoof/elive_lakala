// Copyright 2012 Square, Inc.
package com.lakala.ui.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.lakala.ui.R;

import static android.view.View.MeasureSpec.AT_MOST;
import static android.view.View.MeasureSpec.EXACTLY;
import static android.view.View.MeasureSpec.makeMeasureSpec;

/**
 * ViewGroup that draws a grid of calendar cells.  All children must be {@link CalendarRowView}s.
 * The first row is assumed to be a header and no divider is drawn above it.
 */
public class CalendarGridView extends ViewGroup {
    private final Paint dividerPaint = new Paint();
    private final Paint todayPaint = new Paint();

    //标记今天的坐标位置，绘制边框
    private Rect todayRect = new Rect();

    private int oldWidthMeasureSize;
    private int oldNumRows;

    public CalendarGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        dividerPaint.setColor(getResources().getColor(R.color.l_calendar_divider));
        todayPaint.setColor(getResources().getColor(R.color.l_calendar_blue_00AEFF));
        todayPaint.setStyle(Paint.Style.STROKE);
        todayPaint.setStrokeWidth(2);
    }

    @Override
    public void addView(View child, int index, LayoutParams params) {
      //使日历头和日期内容的高度保持一致，此处注销掉日历头属性的设置
//    if (getChildCount() == 0) {
//      ((CalendarRowView) child).setIsHeaderRow(true);
//    }
        super.addView(child, index, params);
    }

    /**
     * dispatchDraw首先执行，在dispatchDraw中执行super.dispatchDraw，然后调用drawChild方法，绘制子view
     * 子view绘制完成之后，绘制纵向线条,不能先绘制纵向线条，不然绘制子view的时候会将其线条覆盖掉
     * @param canvas
     */
    @Override
    protected void dispatchDraw(Canvas canvas) {
        Logr.d("dispatchDraw time:%d", System.currentTimeMillis());
        super.dispatchDraw(canvas);
        Logr.d("dispatchDraw after super time:%d", System.currentTimeMillis());

        final ViewGroup row = (ViewGroup) getChildAt(0);
        int top = row.getTop();
        int bottom = getBottom();
        // Left side border.
        final int left = row.getChildAt(0).getLeft() + getLeft();
//        canvas.drawLine(left, top, left, bottom, dividerPaint);

        // Each cell's right-side border.
        for (int c = 0; c < 6; c++) {
            int x = left + row.getChildAt(c).getRight();
            canvas.drawLine(x, top, x, bottom, dividerPaint);
        }

        //绘制今天的边框,绘制完成将边框坐标清空，防止view在listview中复用
        if (!todayRect.isEmpty()){
            canvas.drawRect(todayRect,todayPaint);
            todayRect.setEmpty();
        }

    }

    /**
     * 绘制子view，此类中即绘制每周的数据,每周的子view绘制完成之后，开始绘制横向线条
     * @param canvas
     * @param child
     * @param drawingTime
     * @return
     */
    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        Logr.d("drawChild time:%d", System.currentTimeMillis());
        final boolean retVal = super.drawChild(canvas, child, drawingTime);
        // Draw a bottom border.
        final int bottom = child.getBottom() - 1;
        canvas.drawLine(child.getLeft(), bottom, child.getRight()+1, bottom, dividerPaint);

        //查找今天的位置，并记录对应位置坐标
        final CalendarRowView rowView = (CalendarRowView) child;
        for (int cellIndex = 0; cellIndex < 7; cellIndex++) {
            if (rowView.getChildAt(cellIndex) instanceof CalendarCellView) {
                final CalendarCellView cellView = (CalendarCellView) rowView.getChildAt(cellIndex);
                if (cellView.isToday()) {
                    todayRect.set(cellView.getLeft()+2,
                            child.getTop()-1,
                            cellView.getRight()+2,
                            child.getBottom()-1);
                    break;
                }
            }
        }
        return retVal;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Logr.d("Grid.onMeasure w=%s h=%s", MeasureSpec.toString(widthMeasureSpec),
                MeasureSpec.toString(heightMeasureSpec));
        int widthMeasureSize = MeasureSpec.getSize(widthMeasureSpec);
        if (oldWidthMeasureSize == widthMeasureSize) {
            Logr.d("SKIP Grid.onMeasure");
            setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
            return;
        }
        long start = System.currentTimeMillis();
        oldWidthMeasureSize = widthMeasureSize;
        int cellSize = widthMeasureSize / 7;
        // Remove any extra pixels since /7 is unlikely to give whole nums.
        widthMeasureSize = cellSize * 7;
        int totalHeight = 0;
        final int rowWidthSpec = makeMeasureSpec(widthMeasureSize, EXACTLY);
        final int rowHeightSpec = makeMeasureSpec(cellSize, EXACTLY);
        for (int c = 0, numChildren = getChildCount(); c < numChildren; c++) {
            final View child = getChildAt(c);
            if (child.getVisibility() == View.VISIBLE) {
                if (c == 0) { // It's the header: height should be wrap_content.
                    measureChild(child, rowWidthSpec, makeMeasureSpec(cellSize, AT_MOST));
                } else {
                    measureChild(child, rowWidthSpec, rowHeightSpec);
                }
                totalHeight += child.getMeasuredHeight();
            }
        }
        final int measuredWidth = widthMeasureSize + 2; // Fudge factor to make the borders show up.
        setMeasuredDimension(measuredWidth, totalHeight);
        Logr.d("Grid.onMeasure %d ms", System.currentTimeMillis() - start);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        long start = System.currentTimeMillis();
        top = 0;
        for (int c = 0, numChildren = getChildCount(); c < numChildren; c++) {
            final View child = getChildAt(c);
            final int rowHeight = child.getMeasuredHeight();
            child.layout(left, top, right, top + rowHeight);
            top += rowHeight;
        }
        Logr.d("Grid.onLayout %d ms", System.currentTimeMillis() - start);
    }

    public void setNumRows(int numRows) {
        if (oldNumRows != numRows) {
            // If the number of rows changes, make sure we do a re-measure next time around.
            oldWidthMeasureSize = 0;
        }
        oldNumRows = numRows;
    }
}
