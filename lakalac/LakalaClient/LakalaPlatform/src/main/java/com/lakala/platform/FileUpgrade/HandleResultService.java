package com.lakala.platform.FileUpgrade;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.os.Process;

import com.lakala.core.fileupgrade.FileEntity;

import java.util.Vector;

/**
 * <p>Description  : 处理下载结果服务.
 *                   处理非UI展示的结果.</p>
 * <p/>
 * <p>Author       : wangchao.</p>
 * <p>Date         : 14/12/21.</p>
 * <p>Time         : 上午10:53.</p>
 */
public class HandleResultService extends Service {

    final private static String THREAD_NAME = "HandleResultService";

    final public static String ACTION_KEY                = "action_key";
    final public static int    CHECK_SINGLE_SUCCESS      = 2;
    final public static int    STOP_SELF                 = 3;

    private ServiceHandler serviceHandler;

    /*                  handler                 */
    final private class ServiceHandler extends Handler{

        public ServiceHandler(Looper looper){
            super(looper);
        }

        @Override public void handleMessage(Message msg) {

            switch (msg.what){
                case CHECK_SINGLE_SUCCESS:
                    handleCheckSingleSuccess(msg.arg1);
                    break;
                case STOP_SELF:
                    stopSelf(msg.arg1);
                    break;
            }
        }
    }

    @Override public void onCreate() {

        HandlerThread handlerThread = new HandlerThread(THREAD_NAME, Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();

        serviceHandler = new ServiceHandler(handlerThread.getLooper());
    }

    @Override public IBinder onBind(Intent intent) {
        return null;
    }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {

        int action = intent.getIntExtra(ACTION_KEY, 0);
        sendMessage(action, startId);

        return START_REDELIVER_INTENT;
    }

    /**
     * 处理单个文件下载成功，现在只处理解压非 bundle 文件
     *
     * @param startId  service id
     */
    private void handleCheckSingleSuccess(final int startId){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Vector<FileEntity> successList = FileUpgradeManager.getInstance().getSuccessEntityList();
                for (FileEntity entity : successList) {
                    if (!entity.isBundleFile()){
                        entity.decompressEntity();
                    }
                }
                sendMessage(STOP_SELF, startId);
            }
        }).start();
    }

    private void sendMessage(int action, int startId){
        Message message = serviceHandler.obtainMessage();
        message.what    = action;
        message.arg1    = startId;

        serviceHandler.sendMessage(message);
    }
}
