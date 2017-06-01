package com.lakala.core.scanner.bankcard;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.lakala.core.R;
import com.lakala.core.scanner.zxing.camera.CameraManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andy_lv on 14-1-1.
 */
public class BankcardScanView extends View {

    private final String TAG = "ViewfinderView";

    private static final int[] SCANNER_ALPHA = { 0, 64, 128, 192, 255, 192,
            128, 64 };
    private static final long ANIMATION_DELAY = 40L;
    private static final int CURRENT_POINT_OPACITY = 0xA0;
    private static final int MAX_RESULT_POINTS = 20;
    private static final int POINT_SIZE = 10;

    private static final int min_width=500;
    private static final int min_height = 800;

    /**
     * 字体大小
     */
    private static final float TEXT_SIZE = 14;

    /**
     * 四个绿色边角长方形的长
     */
    private final int CORNER_LENGTH = 40;
    private final int CORNER_WIDTH = 6;

    private CameraManager cameraManager;
    private final TextPaint paint;

    private final int maskColor;
    private final int laserColor;
    private final int resultPointColor;
    private final int fontColor;

    private List<ResultPoint> possibleResultPoints;
    private List<ResultPoint> lastPossibleResultPoints;

    private int current_middle_line_top_postion = 0;
    private StaticLayout staticLayout;

    private float density;
    private float textHeight;
    private int  text_length;

    private Rect frame,  previewFrame;

    private int marginTop = 0;
    public BankcardScanView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Initialize these once for performance rather than calling them every
        // time in onDraw().
        paint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        Resources resources = getResources();
        maskColor = resources.getColor(R.color.core_viewfinder_mask);
        laserColor = resources.getColor(R.color.core_viewfinder_laser);
        resultPointColor =resources.getColor(R.color.core_possible_result_points);
        fontColor = resources.getColor(R.color.font_color);

        setMiddleText(getResources().getString(R.string.bankcard_default_status),context);

        possibleResultPoints = new ArrayList<ResultPoint>(5);
    }

    /**
     *
     * @param text
     * @param context
     */
    private void setMiddleText(String text,Context context){
        density = context.getResources().getDisplayMetrics().density;
        text_length = text.length();
        Paint.FontMetrics fm = paint.getFontMetrics();
        textHeight = (float) Math.ceil(fm.descent - fm.ascent);
        paint.setTextSize(TEXT_SIZE * density);
        staticLayout = new StaticLayout(text,  paint, (int) (TEXT_SIZE * density), Layout.Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
    }


    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (cameraManager == null) {
            return; // not ready yet, early draw before done configuring
        }

        frame = cameraManager.getFramingRect();
        previewFrame = cameraManager.getFramingRectInPreview();
        if (frame == null || previewFrame == null) {
            return;
        }
        frame =computeRealSize(frame);
        marginTop = (getHeight()-frame.height())/2;
        drawContent(canvas);
       // drawMiddleLine(canvas);
        drawSanCircle(canvas);
        drawText(canvas);
        // Request another update at the animation interval, but only repaint
        // the laser line,
        // not the entire viewfinder mask.
        postInvalidateDelayed(ANIMATION_DELAY, frame.left + POINT_SIZE,
                frame.top + POINT_SIZE, frame.right - POINT_SIZE-marginTop, frame.bottom
                - POINT_SIZE -marginTop);
    }

    /**
     * 与银行卡扫描区域比较大小
     * @param originalRect
     * @return
     */
    private Rect computeRealSize(Rect originalRect){
        Rect rect =new Rect();
        if(originalRect.width()<min_width){
           int width_diff= (min_width  - originalRect.width())/2;
            rect.left  = originalRect.left  - width_diff;
            rect.right = originalRect.right + width_diff;
        }else{
            rect.left = originalRect.left;
            rect.right = originalRect.right;
        }
        if(originalRect.height()<min_height){
            int height_diff = (min_height - originalRect.height())/2;
            rect.top    = originalRect.top    -  height_diff;
            rect.bottom = originalRect.bottom +  height_diff;
        }else{
            rect.top = originalRect.top;
            rect.bottom = originalRect.bottom;
        }
        return rect;
    }



    /**
     * 画静态图
     * @param canvas
     */
    private void drawContent(Canvas canvas){
        int width = canvas.getWidth();
        int height = canvas.getHeight();
        // Draw the exterior (i.e. outside the framing rect) darkened
        paint.setColor(maskColor);
        canvas.drawRect(0, 0, width, frame.top-marginTop, paint);
        canvas.drawRect(0, frame.top-marginTop, frame.left, frame.bottom + 1-marginTop, paint);
        canvas.drawRect(frame.right + 1, frame.top-marginTop, width, frame.bottom + 1-marginTop,
                paint);
        canvas.drawRect(0, frame.bottom + 1-marginTop, width, height, paint);

        paint.setColor(laserColor);
        paint.setAlpha(SCANNER_ALPHA[4]);
        canvas.drawRect(frame.left + 1, frame.top + 1-marginTop, frame.left
                + CORNER_LENGTH, frame.top + CORNER_WIDTH-marginTop, paint);
        canvas.drawRect(frame.left + 1, frame.top + 1-marginTop, frame.left
                + CORNER_WIDTH, frame.top + CORNER_LENGTH-marginTop, paint);
        canvas.drawRect(frame.right - CORNER_LENGTH, frame.top + 1-marginTop,
                frame.right, frame.top + CORNER_WIDTH -marginTop, paint);
        canvas.drawRect(frame.right - CORNER_WIDTH, frame.top-marginTop, frame.right,
                frame.top + CORNER_LENGTH -marginTop, paint);
        canvas.drawRect(frame.left + 1, frame.bottom - CORNER_WIDTH -marginTop, frame.left
                + CORNER_LENGTH, frame.bottom -marginTop, paint);
        canvas.drawRect(frame.left + 1, frame.bottom -marginTop,
                frame.left + CORNER_WIDTH, frame.bottom - CORNER_LENGTH -marginTop, paint);
        canvas.drawRect(frame.right - CORNER_WIDTH, frame.bottom
                - CORNER_LENGTH -marginTop, frame.right, frame.bottom -marginTop, paint);
        canvas.drawRect(frame.right - CORNER_LENGTH, frame.bottom
                - CORNER_WIDTH -marginTop, frame.right, frame.bottom -marginTop, paint);

    }

    /**
     * 画文字
     * @param canvas
     */
    private void drawText(Canvas canvas){
       /* paint.setColor(fontColor);
        paint.setAlpha(100);
        float x = frame.left + (frame.width()- - TEXT_SIZE * density )/ 2;
        float y = frame.top + (frame.height() - TEXT_SIZE * density * text_length) / 2;
        canvas.translate(x, y);
        staticLayout.draw(canvas);*/


        // 将字竖向写出来
            paint.setColor(Color.RED);
            paint.setAlpha(100);
           float x = frame.left + (frame.width())/ 2;
            float y = frame.top-marginTop + (frame.height() - TEXT_SIZE * density * text_length) / 2;
            canvas.translate(x, y);
            staticLayout.draw(canvas);
    }


    /**
     * 画中间线
     * @param canvas
     */
    private void drawMiddleLine(Canvas canvas){
        if (current_middle_line_top_postion < frame.top
                || current_middle_line_top_postion > frame.bottom) {
            current_middle_line_top_postion = frame.top-marginTop;
        }
        paint.setColor(laserColor);
        paint.setAlpha(SCANNER_ALPHA[4]);
        canvas.drawRect(frame.left + 2, current_middle_line_top_postion - 1,
                frame.right - 1, current_middle_line_top_postion + 2, paint);
        current_middle_line_top_postion += 5;
    }

    /**
     * 花扫描点
     * @param canvas
     */
    private void drawSanCircle(Canvas canvas){
        float scaleX = frame.width() / (float) previewFrame.width();
        float scaleY = frame.height() / (float) previewFrame.height();

        List<ResultPoint> currentPossible = possibleResultPoints;
        List<ResultPoint> currentLast = lastPossibleResultPoints;
        int frameLeft = frame.left;
        int frameTop = frame.top -marginTop;
        if (currentPossible.isEmpty()) {
            lastPossibleResultPoints = null;
        } else {
            possibleResultPoints = new ArrayList<ResultPoint>(5);
            lastPossibleResultPoints = currentPossible;
            paint.setAlpha(CURRENT_POINT_OPACITY);
            paint.setColor(resultPointColor);
            synchronized (currentPossible) {
                for (ResultPoint point : currentPossible) {
                    canvas.drawCircle(
                            frameLeft + (int) (point.getX() * scaleX), frameTop
                            + (int) (point.getY() * scaleY),
                            POINT_SIZE, paint);
                }
            }
        }
        if (currentLast != null) {
            paint.setAlpha(CURRENT_POINT_OPACITY / 2);
            paint.setColor(resultPointColor);
            synchronized (currentLast) {
                float radius = POINT_SIZE / 2.0f;
                for (ResultPoint point : currentLast) {
                    canvas.drawCircle(
                            frameLeft + (int) (point.getX() * scaleX), frameTop
                            + (int) (point.getY() * scaleY), radius,
                            paint);
                }
            }
        }
    }

    /**
     *
     * @param point
     */
    public void addPossibleResultPoint(ResultPoint point) {
        List<ResultPoint> points = possibleResultPoints;
        synchronized (points) {
            points.add(point);
            int size = points.size();
            if (size > MAX_RESULT_POINTS) {
                // trim it
                points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
            }
        }
    }
}
