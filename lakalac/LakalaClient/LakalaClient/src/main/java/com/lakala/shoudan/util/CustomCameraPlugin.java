package com.lakala.shoudan.util;

import com.lakala.platform.nativeplugin.CameraPlugin;
import com.lakala.shoudan.activity.CustomerCameraActivity;

import java.io.File;

/**
 * Created by linmq on 2016/6/23.
 */
public class CustomCameraPlugin extends CameraPlugin {
    private int overlayType = 1;

    public void startActionImageCaptureCustom() {
        tempFile = new File(getFilePath(), System.currentTimeMillis() + ".jpg");
        CustomerCameraActivity.startForResult(this, tempFile.getPath(), getOverlayType());
    }

    public int getOverlayType() {
        return overlayType;
    }

    public CustomCameraPlugin setOverlayType(int overlayType) {
        this.overlayType = overlayType;
        return this;
    }
}
