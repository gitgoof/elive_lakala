package com.lakala.shoudan.util;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lakala.ui.common.Dimension;
import com.lakala.ui.module.lockpattern.LockPatternView;

/**
 * 根据不同屏幕适配
 *
 * Created by jerry on 14-4-29.
 */
public class ScreenAdapter {

    public static void lockViewAdapter(Activity context,LockPatternView lockPatternView) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        LinearLayout.LayoutParams params = null;
        if (displayMetrics.widthPixels == 320){
            params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    Dimension.dip2px(240, context)
            );
        }else if (displayMetrics.widthPixels == 480){
            if (displayMetrics.heightPixels < 750){
                params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Dimension.dip2px(260, context)
                );
            }else {
                params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Dimension.dip2px(280, context)
                );
            }
        }else if (displayMetrics.widthPixels == 800 && displayMetrics.heightPixels == 1280){
            params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    Dimension.dip2px(360, context)
            );
        }

        if (params != null)
            lockPatternView.setLayoutParams(params);
    }

}
