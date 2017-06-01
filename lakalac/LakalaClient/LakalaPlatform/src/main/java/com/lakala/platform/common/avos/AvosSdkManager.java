package com.lakala.platform.common.avos;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVInstallation;
import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVOSServices;
import com.avos.avoscloud.PushService;
import com.avos.avoscloud.SaveCallback;
import com.lakala.library.DebugConfig;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.PropertiesUtil;

/**
 * Avos sdk 初始化控制类
 * Created by More on 15/1/29.
 */
public class AvosSdkManager {

    public static void init(){

        AVOSCloud.useAVCloudCN();
        //消息推送
        String appId = DebugConfig.DEV_ENVIRONMENT ?
                "1ffm14xo560yivlafxuxxfgco76dml8wmfd87aeio07kiopk"
                :"a7or8vg8kl1i9xuhjwhjvqr7fxut22h4z7o49e468h4b741z";
        String clientKey = DebugConfig.DEV_ENVIRONMENT ?
                "cvsv00lcfqm6blc1rkct6u5y0qivczqzbc3tydlsjsn5adxd"
                : "k7d2eyb55y1623742v4iqo7il8rem9kav605jkz53y06u0ao";AVOSCloud.initialize(ApplicationEx.getInstance(), appId, clientKey);        //数据统计初始化

        String baseUrl =
                DebugConfig.DEV_ENVIRONMENT ?
                "http://10.5.31.10:8182" :
                "https://mposc.lakala.com:6443/avosCollect";
        AVOSCloud.setModuleServers(AVOSServices.STATISTICS_SERVICE, baseUrl);
        AVAnalytics.setSessionContinueMillis(60 * 1000);
        AVAnalytics.start(ApplicationEx.getInstance());
        AVAnalytics.enableCrashReport(ApplicationEx.getInstance(), true);
    }


    public static void initPush(Context context, Intent intent, Class clazz){

        //数据统计
        AVAnalytics.trackAppOpened(intent);

        //初始化消息推送
        AVInstallation.getCurrentInstallation().saveInBackground(new SaveCallback() {
            @Override
            public void done(AVException e) {
                //保存上送的InstallationId
                installationId = AVInstallation.getCurrentInstallation().getInstallationId();
                AVInstallation.getCurrentInstallation().getObjectId();
            }
        });
        PushService.setDefaultPushCallback(context, clazz);

    }

    public static String installationId;



}
