package com.lakala.platform.activity;

import android.view.View;

import com.lakala.core.base.LKLActivity;
import com.lakala.core.base.LKLActivityDelegate;
import com.lakala.platform.launcher.BusinessLauncher;

/**
 * Created by Michael on 14-12-3.
 */
public class BaseActivity extends LKLActivity implements View.OnClickListener{
    /**
     * 页面请求码
     */
    public static final int REQUEST_CODE_LOGIN = 32;
    public static final int REQUEST_CODE_LOGIN_GESTURE = 33;
    public static final int REQUEST_CODE_LOCK_SCREEN = 34;
    public static final int REQUEST_CODE_TO_PAY = 35;

    //点击同一 view 最小的时间间隔，如果小于这个数则忽略此次单击。
    private static long intervalTime = 800;
    //最后点击时间
    private long lastClickTime = 0;
    //最后被单击的 View 的ID
    private long lastClickView = 0;

    @Override
    protected LKLActivityDelegate delegate() {
        return BusinessLauncher.getInstance();
    }

    @Override
    public void onClick(View view) {
        if (!isFastMultiClick(view)) {
            onViewClick(view);
        }
    }

    /**
     * 防短时重复点击回调 <br>
     * 子类使用 View.OnClickListener 设置监听事件时直接覆写该方法完成点击回调事件
     *
     * @param view 被单击的View
     */
    protected void onViewClick(View view) {
        //供字类重写用事件
    }

    /**
     * 是否快速多次点击(连续多点击）
     * @param  view 被点击view，如果前后是同一个view，则进行双击校验
     * @return 认为是重复点击时返回true。
     */
    private boolean isFastMultiClick(View view) {
        long time = System.currentTimeMillis() - lastClickTime;

        if (time < intervalTime && lastClickView == view.getId()) {
            lastClickTime = System.currentTimeMillis();
            return true;
        }

        lastClickTime = System.currentTimeMillis();
        lastClickView = view.getId();

        return false;
    }
}
