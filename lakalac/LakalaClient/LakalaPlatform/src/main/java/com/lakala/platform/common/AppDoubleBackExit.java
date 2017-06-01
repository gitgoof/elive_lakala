package com.lakala.platform.common;

import android.app.Activity;
import android.view.Gravity;
import android.widget.Toast;

import com.lakala.library.util.ToastUtil;
import com.lakala.platform.R;

/**
 * 双击Back退出
 *
 * Created by jerry on 14-2-19.
 */
public class AppDoubleBackExit {

    //缓存退出事件
    private static long cacheMillisecond = 0;

    public static void onBack(Activity context){

        if (context.isFinishing()) return;

        long millisecond = System.currentTimeMillis();

        //第一次Back记录时间
        if (cacheMillisecond == 0){
            ToastUtil.toast(context,context.getResources().getString(R.string.agin_exit), Toast.LENGTH_LONG);
            cacheMillisecond = millisecond;
            return;
        }
        //两秒内第二次点击Back键直接退出
        if ((millisecond - cacheMillisecond) <= 2000){
            ApplicationEx.getInstance().exit();
        }else {
            ToastUtil.toast(context,context.getResources().getString(R.string.agin_exit), Toast.LENGTH_LONG);
            cacheMillisecond = millisecond;
        }
    }

}
