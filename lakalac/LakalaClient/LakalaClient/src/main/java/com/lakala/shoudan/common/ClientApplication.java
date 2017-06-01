package com.lakala.shoudan.common;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.lakala.library.DebugConfig;
import com.lakala.library.exception.CrashHandler;
import com.lakala.library.util.CertificateCode;
import com.lakala.library.util.LogUtil;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.PropertiesUtil;
import com.lakala.platform.customactivityoncrash.CustomActivityOnCrash;
import com.lakala.shoudan.BuildConfig;
import com.lakala.shoudan.activity.shoudan.loan.location.U51LocationHelper;
import com.lakala.shoudan.loginservice.TokenRefreshService;
import com.paem.framework.basiclibrary.log.PALog;
import com.treefinance.sdk.GFDAgent;

import java.io.DataOutputStream;
import java.io.File;

/**
 * Created by More on 15/3/21.
 */
public class ClientApplication extends ApplicationEx {

    @Override
    public void onCreate(){
        super.onCreate();
        CertificateCode.envir=BuildConfig.FLAVOR;
        bindTokenRefreshService();
        boolean useCustom = false;
        if(useCustom){
            CustomActivityOnCrash.install(this);
        }else {
            CrashHandler.init(new CrashHandler.OnHandleCrashExceptionListener() {
                @Override
                public void handleException(Throwable ex) {

                    exit();

                }
            },null);
        }
//        Toast.makeText(ClientApplication.this, ""+getRootAhth(), Toast.LENGTH_SHORT).show();

    }

//    public synchronized boolean getRootAhth()
//    {
//        Process process = null;
//        DataOutputStream os = null;
//        try{
//            process = Runtime.getRuntime().exec("su");
//            os = new DataOutputStream(process.getOutputStream());
//            os.writeBytes("exit\n");
//            os.flush();
//            int exitValue = process.waitFor();
//            if (exitValue == 0)
//            {
//                return true;
//            } else{
//                return false;
//                }
//            } catch (Exception e){
//            Log.d("*** DEBUG ***", "Unexpected error - Here is what I know: "
//                    + e.getMessage());
//            return false;
//            } finally{
//            try {
//                if (os != null){
//                    os.close();
//                    }
//                process.destroy();
//            } catch (Exception e){
//                e.printStackTrace();
//                }
//            }
//        }

    @Override
    protected void initConfig() {
        DebugConfig.DEV_ENVIRONMENT = !BuildConfig.URL.contains("mpos.lakala.com");
        DebugConfig.DEBUG = BuildConfig.DEBUG;
        PropertiesUtil.setUrl(BuildConfig.URL);

        GFDAgent.init(this);//功夫贷初始化
        //InitializaU51LoanSDK.initLoanSDK(this, null);
        U51LocationHelper.getInstance().initialize(this);//51贷初始化

        com.lakala.shoudan.activity.shoudan.loan.kepler.CrashHandler.getInstance().init(getApplicationContext());
        PALog.IS_SECURITY_LOG = false;
        PALog.IS_LOG_POSITION = true;
        PALog.IS_DEBUG = true;
        PALog.DEBUG_LEVEL = PALog.LEVEL_V;
        PALog.setLogSaveLevel(PALog.LEVEL_V);
        PALog.SAVE_MODE = PALog.SAVE_MODE_2;
        PALog.LOG_DIR = this.getPackageName() + "_log";
        PALog.e("demo","开谱勒初始化了====================");
        super.initConfig();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }

    private void bindTokenRefreshService(){

        Intent intent = new Intent(this, TokenRefreshService.class);
        bindService(intent,serviceConnection, BIND_AUTO_CREATE);

    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            LogUtil.print(getClass().getName(), "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            LogUtil.print(getClass().getName(), "onServiceDisconnected");
        }
    };

    private void unbindTokenRefreshService(){

        unbindService(serviceConnection);

    }


    public boolean isRoot() {

        boolean root = false;

        try {
            if ((!new File("/system/bin/su").exists())
                    && (!new File("/system/xbin/su").exists())) {
                root = false;
            } else {
                root = true;
            }

        } catch (Exception e) {
        }

        return root;
    }

}
