package com.lakala.platform.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.MutexThreadManager;
import com.lakala.platform.swiper.devicemanager.SwiperInfo;
import com.lakala.platform.swiper.devicemanager.SwiperManagerCallback;
import com.lakala.platform.swiper.devicemanager.SwiperManagerHandler;
import com.lakala.platform.swiper.devicemanager.SwiperProcessState;
import com.lakala.platform.swiper.devicemanager.connection.MultiConnectionManager;
import com.lakala.platform.swiper.devicemanager.controller.SwiperManagerHandler2;

/**
 * Created by More on 15/1/16.
 */
public abstract class BasePaymentActivity2 extends BaseActionBarActivity implements SwiperManagerCallback{

    protected SwiperManagerHandler2 swiperManagerHandler;

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initSwiper();

    }


    @Override
    protected void onResume() {
        super.onResume();
        swiperManagerHandler.checkConnection(this);
    }

    /**** 以下为必须实现的方法 ****/
    public void initSwiper() {
        //界面初始化完成,监听刷卡器
        swiperManagerHandler = new SwiperManagerHandler2(this, MultiConnectionManager.class);
        swiperManagerHandler.setSwiperManagerCallback(this);
        return;
    }

    @Override
    public void onNotifyFinish(String error) {

        LogUtil.print("<AS>","finish");
        finish();

    }

    protected abstract void startPayment(SwiperInfo swiperInfo);

    /** 交易 **/
    @Override
    public void onMscProcessEnd(final SwiperInfo swiperInfo) {

        swiperManagerHandler.setOnlineHappen(true);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startPayment(swiperInfo);

            }
        });
    }

    @Override
    public void onRequestUploadIC55(final SwiperInfo swiperInfo) {
        LogUtil.print("<AS>","onRequestUploadIC55");
        swiperManagerHandler.setOnlineHappen(true);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startPayment(swiperInfo);

            }
        });
    }

    @Override
    public void requestUploadTc(SwiperInfo swiperInfo) {
        LogUtil.print("<AS>","requestUploadTc");
        LogUtil.print(getClass().getName(), "SwiperInfo =" + swiperInfo.toString());
    }

    @Override
    public void onProcessEvent(final SwiperProcessState swiperProcessState, SwiperInfo swiperInfo) {
        LogUtil.print("<AS>","onProcessEvent");

    }

    protected void doSecIss(final boolean transRslt, final String icc55, final String authCode){
        MutexThreadManager.runThread("sec", new Runnable() {
            @Override
            public void run() {
                swiperManagerHandler.doSecondIssuance(transRslt, icc55, authCode);
            }
        });

    }


    @Override
    protected void onPause() {
        super.onPause();
        swiperManagerHandler.reset();

    }

    @Override
    protected void onDestroy() {

        try{

            swiperManagerHandler.release();
            swiperManagerHandler = null;

        }catch (Exception e){
            LogUtil.print(e);
        }
        super.onDestroy();
    }

}
