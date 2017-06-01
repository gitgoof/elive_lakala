package com.lakala.core.schedule;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 计划任务广播接收分发类;
 */
public class ScheduleReceiver extends BroadcastReceiver {

    protected void onReceive(Context context,String uniqueKey){

    }

    @Override
    public final void onReceive(Context context, Intent intent) {
        String uniqueKey            = intent.getStringExtra(ScheduleManager.class.getName());
        ScheduleDate date           = intent.getParcelableExtra(ScheduleDate.class.getName());
        ScheduleExecutor executor   = (ScheduleExecutor) intent.getSerializableExtra(ScheduleExecutor.class.getName());

        date.checkState(context, uniqueKey, executor);
        executor.execute(context);

        this.onReceive(context, uniqueKey);
    }


}
