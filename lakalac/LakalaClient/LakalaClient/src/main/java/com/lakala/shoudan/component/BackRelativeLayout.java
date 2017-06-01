package com.lakala.shoudan.component;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.RelativeLayout;

/**
 * Created by ZhangMY on 2015/3/19.
 */
public class BackRelativeLayout extends RelativeLayout{
    public BackRelativeLayout(Context context) {
        super(context);
    }

    public BackRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Activity mSearchActivity;

    public void setmSearchActivity(Activity mSearchActivity){
        this.mSearchActivity = mSearchActivity;
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (mSearchActivity != null &&
                event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            KeyEvent.DispatcherState state = getKeyDispatcherState();
            if (state != null) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getRepeatCount() == 0) {
                    state.startTracking(event, this);
                    return true;
                } else if (event.getAction() == KeyEvent.ACTION_UP
                        && !event.isCanceled() && state.isTracking(event)) {
                    mSearchActivity.onBackPressed();
                    return true;
                }
            }
        }

        return super.dispatchKeyEventPreIme(event);
    }
}
