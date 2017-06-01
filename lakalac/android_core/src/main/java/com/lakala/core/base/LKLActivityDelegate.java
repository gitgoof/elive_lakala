package com.lakala.core.base;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by wangchao on 14/11/11.
 */
public interface LKLActivityDelegate {

    public void onCreate(Activity me, Bundle savedInstanceState);

    public void onStart(Activity me);

    public void onResume(Activity me);

    public void onPause(Activity me);

    public void onStop(Activity me);

    public void onDestroy(Activity me);
}
