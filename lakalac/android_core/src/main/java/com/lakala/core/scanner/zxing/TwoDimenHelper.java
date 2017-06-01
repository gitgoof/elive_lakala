package com.lakala.core.scanner.zxing;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;

import com.google.zxing.Result;
import com.lakala.core.scanner.bankcard.BankcardScanView;
import com.lakala.core.scanner.zxing.camera.CameraManager;

/**
 * Created by andy_lv on 14-1-14.
 */
public interface TwoDimenHelper {
    public   ViewfinderView getViewfinderView();
    public   Handler getHandler();
    public   CameraManager getCameraManager();

    public BankcardScanView getBankcardScanView();

    public  void handleDecode(Result rawResult, Bitmap barcode, float scaleFactor);

    public void  setResult(int ReturnType,Intent intent);

    public void finish();

    public Activity getActivity();

    public void restartPreviewAfterDelay(long delayMS);
}
