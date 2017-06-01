package com.lakala.core.schedule;

import android.content.Context;

import java.io.Serializable;

/**
 * Created by LL on 13-12-25.
 */
public abstract class ScheduleExecutor implements Serializable{

    /**
     * 具体的业务逻辑
     * @param context
     */
    public abstract void execute(Context context);
}

