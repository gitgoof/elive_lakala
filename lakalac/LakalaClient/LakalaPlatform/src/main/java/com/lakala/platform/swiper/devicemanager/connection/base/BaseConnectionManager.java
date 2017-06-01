package com.lakala.platform.swiper.devicemanager.connection.base;

import android.app.Activity;
import android.content.Context;

import com.lakala.library.util.LogUtil;

/**
 * Created by More on 14-8-25.
 */
public abstract class BaseConnectionManager implements ConnectionMangerInt {

    protected ConnectionStateListener listener;
    protected Context context;

    public BaseConnectionManager(Context context, ConnectionStateListener listener){
        this.listener = listener;
        this.context = context;
    }

    public void runOnUiThread(Runnable runnable){

        if(pausing){
            return;
        }

        if(context != null){
            try{
                ((Activity)context).runOnUiThread(runnable);
            }catch (Exception e){
                LogUtil.print(e);
            }
        }else{
//            throw  new RuntimeException("context == null");
        }

    }

    public void finish(){
        LogUtil.print("Interrupted and finish");
        if(context != null){
            ((Activity) context).finish();
        }

    }

    private boolean pausing = false;

    public void pause(){
        pausing = true;
    }

    public void resume(){
        pausing = false;
    }
}
