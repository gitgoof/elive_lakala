package com.lakala.shoudan.common;

import android.view.View;

import java.util.Calendar;

/**
 * Created by huwei on 2017/2/28.
 */
public abstract class PerfectClickListener implements View.OnClickListener {
    private final long min_click_duration = 1000;
    private int lastId = 0;
    private long lastClickMillis;

    @Override
    public void onClick(View view) {
        long currentMills = Calendar.getInstance().getTimeInMillis();
        int currentId = view.getId();
        if (lastId != currentId) {
            lastId = currentId;
            lastClickMillis = currentMills;
            onNoDoubleClick(view);
            return;
        }
        if (currentMills - lastClickMillis > min_click_duration) {
            lastClickMillis = currentMills;
            onNoDoubleClick(view);
        }

        //相当于点击间隔小于1秒，且两次点击的id一样，不做处理，app无响应即可
    }

    /**
     * 提供点击的方法
     *
     * @param view
     */
    protected abstract void onNoDoubleClick(View view);

}