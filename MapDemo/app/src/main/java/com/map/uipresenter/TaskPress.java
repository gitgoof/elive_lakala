package com.map.uipresenter;

import android.content.Context;

import com.map.uiview.TaskView;

/**
 * Created by xiaogu on 2017/3/13.
 */
public class TaskPress extends BasePress{
    Context context;
    TaskView taskView;

    public TaskPress(Context context) {
        this.context = context;
        this.taskView = (TaskView) context;
    }
}
