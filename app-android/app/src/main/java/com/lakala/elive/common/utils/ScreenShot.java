package com.lakala.elive.common.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ousachisan on 2017/4/24.
 */

public class ScreenShot {

    public static Bitmap takeScreenShot(Activity activity,float h) {
        // View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap b1 = view.getDrawingCache();

        // 获取状态栏高度
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        // 获取屏幕长和高
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        int height = activity.getWindowManager().getDefaultDisplay()
                .getHeight();
        // 去掉标题栏
        Bitmap b = Bitmap.createBitmap(b1, 0, statusBarHeight+DensityUtil.dip2px(activity,h), width, height
                - statusBarHeight-DensityUtil.dip2px(activity,h));
        view.destroyDrawingCache();
        return b;
    }

    private static void savePic(Bitmap b, File filePath) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            // e.printStackTrace();
        } catch (IOException e) {
            // e.printStackTrace();
        }
    }

//    public static void shoot(Activity a, File filePath) {
//        if (filePath == null) {
//            return;
//        }
//        if (!filePath.getParentFile().exists()) {
//            filePath.getParentFile().mkdirs();
//        }
//        ScreenShot.savePic(ScreenShot.takeScreenShot(a), filePath);
//    }
}
