/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lakala.core.scanner.zxing;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.lakala.core.R;
import com.lakala.core.scanner.zxing.camera.CameraManager;
import com.lakala.library.util.DimenUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * This view is overlaid on top of the camera preview. It adds the viewfinder
 * rectangle and partial transparency outside it, as well as the laser scanner
 * animation and result points.
 * 
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class ViewfinderView extends View {

	private final String TAG = "ViewfinderView";

	private static final int[] SCANNER_ALPHA = { 0, 64, 128, 192, 255, 192,
			128, 64 };
    private static final long ANIMATION_DELAY = 20L;
    private static final int CURRENT_POINT_OPACITY = 0xA0;
    private static final int MAX_RESULT_POINTS = 20;
    private static final int POINT_SIZE = 6;

	/**
	 * 四个绿色边角长方形的长
	 */
	private final int CORNER_LENGTH = 40;
	private final int CORNER_WIDTH = 4;

	private CameraManager cameraManager;
	private final Paint paint;
	private final int maskColor;
	private final int laserColor;
	private final int resultPointColor;
	private final int fontColor;
    private int scannerAlpha;

    private List<ResultPoint> possibleResultPoints;
	private List<ResultPoint> lastPossibleResultPoints;

	private int current_middle_line_top_postion = 0;
	
	private Rect frame,  previewFrame;

    private float density;
    private int frame_top ;
    private int marginTop = 0;
    private Bitmap scanLineBitmap;
    private Bitmap leftTopBitmap,rightTopBitmap,leftBottomBitmap,rightBottomBitmap;
    private String message;

    private int frameBottom = 0;

	// This constructor is used when the class is built from an XML resource.
	public ViewfinderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		// Initialize these once for performance rather than calling them every
		// time in onDraw().
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		Resources resources = getResources();
        message =  getResources().getString(R.string.msg_default_status);

		maskColor = resources.getColor(R.color.core_viewfinder_mask);
		laserColor = resources.getColor(R.color.core_viewfinder_laser);
		resultPointColor = resources.getColor(R.color.core_possible_result_points);
		fontColor = resources.getColor(R.color.font_color);
        scanLineBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.core_saoma_line);
        leftTopBitmap  = BitmapFactory.decodeResource(getResources(),R.drawable.img_scanner_frame_3);
        rightTopBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.img_scanner_frame_1);
        rightBottomBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.img_scanner_frame_2);
        leftBottomBitmap = BitmapFactory.decodeResource(getResources(),R.drawable.img_scanner_frame_4);

		possibleResultPoints = new ArrayList<ResultPoint>(5);

        lastPossibleResultPoints = null;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity)getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        density = displayMetrics.density;

	}

	public void setCameraManager(CameraManager cameraManager) {
		this.cameraManager = cameraManager;
	}

    /**
     *  设置message
     * @param message
     */
    public void setMessage(String message){
      this.message = message;
      postInvalidate();
    }

	@Override
	public void onDraw(Canvas canvas) {
		if (cameraManager == null) {
			return; // not ready yet, early draw before done configuring
		}
		
		frame = cameraManager.getFramingRect();
	    previewFrame = cameraManager.getFramingRectInPreview();
		if (frame == null || previewFrame == null) {
			return;
		}
        frame_top = (frame.height()-frame.width())/2;
        marginTop = DimenUtil.dp2px(getContext(),45);
        if(Build.HARDWARE.contains("mx")){
            marginTop = marginTop+DimenUtil.dp2px(getContext(),48)/2;
        }
        frameBottom =frame.bottom-marginTop;
        drawContent(canvas);
        drawMiddleLine(canvas);
        drawSanCircle(canvas);
        postInvalidateDelayed(ANIMATION_DELAY,
                frame.left,
                frame.top-marginTop+frame_top ,
                frame.right,
                frame.bottom + 1-marginTop-frame_top );

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
        canvas.drawRect(0, 0, width, frame.top-marginTop+frame_top, paint);
		canvas.drawRect(0, frame.top-marginTop+frame_top, frame.left, frame.bottom-marginTop-frame_top, paint);
		canvas.drawRect(frame.right, frame.top-marginTop+frame_top, width, frame.bottom -marginTop-frame_top,
				paint);
		canvas.drawRect(0, frame.bottom-marginTop-frame_top, width, height, paint);

        canvas.drawBitmap(leftTopBitmap,frame.left,frame.top-marginTop+frame_top,paint);
        canvas.drawBitmap(leftBottomBitmap,frame.left,frame.bottom-marginTop-frame_top-leftBottomBitmap.getHeight(),paint);
        canvas.drawBitmap(rightTopBitmap,frame.right-rightTopBitmap.getWidth(),
                frame.top-marginTop+frame_top,paint);
        canvas.drawBitmap(rightBottomBitmap,frame.right-rightTopBitmap.getWidth(),
                frame.bottom-marginTop-frame_top-rightBottomBitmap.getHeight(),paint);

		paint.setColor(fontColor);


		paint.setTextSize(DimenUtil.dp2px(getContext(),13));
		float text_width=paint.measureText(message, 0, message.length());
		int left_offset =(int) ((frame.width()-text_width)/2);
		canvas.drawText(message, 0,message.length(),frame.left+left_offset, frame.bottom+DimenUtil.dp2px(getContext(),30)-marginTop-frame_top, paint);
	}
	
	
	/**
	 * 画中间线
	 * @param canvas
	 */
	private void drawMiddleLine(Canvas canvas){
		if (current_middle_line_top_postion < frame.top-marginTop+frame_top
				|| current_middle_line_top_postion > frame.bottom-marginTop-frame_top-DimenUtil.dp2px(getContext(),9)) {
			current_middle_line_top_postion = frame.top-marginTop+frame_top+DimenUtil.dp2px(getContext(),2);
		}

        Bitmap myScanLineBitmap = Bitmap.createScaledBitmap(scanLineBitmap,frame.width()-DimenUtil.dp2px(getContext(),7),
                scanLineBitmap.getHeight(),true);
        canvas.drawBitmap(myScanLineBitmap,frame.left+DimenUtil.dp2px(getContext(),3),current_middle_line_top_postion,paint);
		current_middle_line_top_postion += DimenUtil.dp2px(getContext(),5);
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
		int frameTop = frame.top - marginTop;
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
									+(int) (point.getY() * scaleY),
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
								+(int) (point.getY() * scaleY), radius,
							paint);
				}
			}
		}
	}

	public void addPossibleResultPoint(ResultPoint point) {
		List<ResultPoint> points = possibleResultPoints;
		synchronized (points) {
			points.add(point);
			int size = points.size();
			if (size > MAX_RESULT_POINTS) {
				points.subList(0, size - MAX_RESULT_POINTS / 2).clear();
			}
		}
	}

    /**
     * 得到Frame bottom
     * @return
     */
    public int getFrameBottom(){
        return this.frameBottom;
    }
}
