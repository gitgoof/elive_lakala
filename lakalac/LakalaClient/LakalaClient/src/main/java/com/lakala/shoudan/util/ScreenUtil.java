package com.lakala.shoudan.util;

import android.app.Activity;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.Display;

import com.lakala.shoudan.common.Parameters;

/**
 * Created by HUASHO on 2015/1/27.
 * 屏幕工具类
 */
public class ScreenUtil {

    public static void getScrrenWidthAndHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        Display display = activity.getWindowManager().getDefaultDisplay();
        display.getMetrics(dm);
        Parameters.screenWidth = display.getWidth();
        Parameters.screenHeight = display.getHeight();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        Parameters.statusBarHeight = frame.top;
    }


}
