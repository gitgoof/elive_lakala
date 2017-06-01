package com.lakala.platform.swiper.mts;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.lakala.library.util.LogUtil;
import com.lakala.library.util.StringUtil;
import com.lakala.platform.common.CrashlyticsUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by wangchao on 14-3-28.
 */
public class GetSwipeKsnTask extends AsyncTask<Object, Object, String> {

    private GetKsnListener listener;

    private SwiperManager manager;

    private String ksn;

    private ExecutorService pool;

    public GetSwipeKsnTask(GetKsnListener listener, SwiperManager manager) {
        this.listener = listener;
        this.manager  = manager;
        this.ksn      = null;
        this.pool     = Executors.newFixedThreadPool(1);
    }

    @Override
    protected void onPreExecute() {
        DialogController.getInstance().showProgress(listener.ksnActivity());
    }

    @Override
    protected String doInBackground(Object[] objects) {
        getKsn();
        return ksn;
    }

    @Override
    protected void onPostExecute(String ksn) {

        DialogController.getInstance().dismiss();
        LogUtil.e("wcwcwc", "ksn : " + ksn);
        if(StringUtil.isEmpty(ksn)){
            listener.GetKsnFailure();
        }else {
            listener.GetKsnSuccess(ksn);
        }
    }

    //getKsn 线程
    private final Runnable GET_KSN_RUN = new Runnable() {
        @Override
        public void run() {
            //crash #354 获取ksn时导致的空指针问题，添加判断，避免因此导致crash
            if(manager != null){
                String tempKsn = manager.getKsn();
                if (!StringUtil.isEmpty(tempKsn)){
                    ksn = tempKsn.toUpperCase();
                }
            }
        }
    };

    /**
     * getKsn
     */
    private void getKsn(){
        if (pool == null){
            return;
        }
        Future future = pool.submit(GET_KSN_RUN);
        try {
            future.get(15, TimeUnit.SECONDS);
        } catch (Exception e) {
            CrashlyticsUtil.logException(e);
            ksn = "";
        }finally {
            if (!future.isCancelled()){
                future.cancel(true);
            }
            if (pool != null && !pool.isShutdown()){
                pool.shutdown();
                pool = null;
            }
        }
    }

    public static interface GetKsnListener {
        void GetKsnSuccess(String ksn);

        void GetKsnFailure();

        FragmentActivity ksnActivity();
    }
}
