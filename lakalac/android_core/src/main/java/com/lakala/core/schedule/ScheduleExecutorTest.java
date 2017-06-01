package com.lakala.core.schedule;

import android.content.Context;
import android.util.Log;

import com.lakala.library.util.LogUtil;

/**
 * Created by LL on 13-12-25.
 */
public class ScheduleExecutorTest extends ScheduleExecutor {
    private static final long serialVersionUID = 4034282727383833272L;

    @Override
    public void execute(Context context) {
        LogUtil.e("Test", "子类的具体业务逻辑~~");
    }

}
