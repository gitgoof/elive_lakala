package com.lakala.shoudan.activity.payment.signature;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

/**
 * Created by More on 14-1-20.
 */
public class SignatruePollingService extends Service{

    private SignatureManager signatureManager;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        signatureManager = SignatureManager.getInstance();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        signatureManager.startUpload(new SignatureManager.UploadListener() {
            @Override
            public void onUploadFinish(boolean ifSuccess) {
                if(ifSuccess){
                    handler.sendEmptyMessage(1);
                }else{
                    handler.sendEmptyMessage(0);
                }
            }
        });
    }

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0://补签出错
                    stopSelf();
                    break;
                case 1://补签成功
                    signatureManager.stopSignatruePollingService();
                    break;
                default:
                    break;

            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
