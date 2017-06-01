package com.lakala.shoudan.activity.main;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.networkbench.agent.impl.NBSAppAgent;
import com.lakala.library.util.LogUtil;
import com.lakala.library.util.ToastUtil;
import com.lakala.platform.FileUpgrade.FileUpgradeManager;
import com.lakala.platform.FileUpgrade.UpgradeResultHandler;
import com.lakala.platform.FileUpgrade.ValidateFileHandler;
import com.lakala.platform.common.AppUpgradeController;
import com.lakala.platform.common.ApplicationEx;
import com.lakala.platform.common.LklPreferences;
import com.lakala.platform.common.PropertiesUtil;
import com.lakala.platform.launcher.BusinessLauncher;
import com.lakala.platform.response.HttpConnectEvent;
import com.lakala.platform.response.ResultServices;
import com.lakala.platform.response.ServiceResultCallback;
import com.lakala.shoudan.BuildConfig;
import com.lakala.shoudan.R;
import com.lakala.shoudan.activity.AppBaseActivity;
import com.lakala.shoudan.activity.shoudan.FloatingLayerActivity;
import com.lakala.shoudan.activity.shoudan.finance.manager.FinanceRequestManager;
import com.lakala.shoudan.bll.service.shoudan.ShoudanService;
import com.lakala.shoudan.bll.task.SplashTask;
import com.lakala.shoudan.common.UniqueKey;
import com.lakala.shoudan.util.NotificationUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Created by LMQ on 2015/11/19.
 */
public class MainActivity_5_0 extends AppBaseActivity {

    private long lastClickTime = 0;
//    DBManager dbManagerMainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        BusinessLauncher.getInstance().finishAll();
        FinanceRequestManager.getInstance().clear();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navigationBar.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, new MainFragment())
                .commit();
        initSplash();
//        dbManager.getIntance(this, ApplicationEx.getInstance().getUser().getLoginName());
        getIcDownEnable();
//        initFloating();
        webAppCheckUpdate();
        getIcDownEnable();
        LogUtil.print("System.out","onCreate");
        NBSAppAgent.setLicenseKey("cdfeeb1dc91343fd9ecab251d60e8f7f").withLocationServiceEnabled(true).start(this.getApplicationContext());
//        testTingyun();
    }



    public void getSign(){

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtil.print("sdk_test","onDestroy");
    }

    private void webAppCheckUpdate() {
        /**
         * webapp检查更新
         */
        registerReceiver();
        FileUpgradeManager.getInstance().init(false);
        NotificationUtil.doNotify();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppUpgradeController appUpgradeController = AppUpgradeController.getInstance();
        appUpgradeController.checkUpgradeMessage(this);
    }


    private ValidateFileHandler validateFileHandler = new ValidateFileHandler();

    private UpgradeResultHandler upgradeResultHandler = new UpgradeResultHandler(this);

    private void registerReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(FileUpgradeManager.ACTION_CHECK_ALL_FINISHED);
        intentFilter.addAction(FileUpgradeManager.ACTION_CHECK_SINGLE_FINISHED);
        registerReceiver(upgradeResultHandler, intentFilter);

        intentFilter = new IntentFilter();
        intentFilter.addAction(FileUpgradeManager.ACTION_LOCAL_FILE_INVALIDED);
        registerReceiver(validateFileHandler, intentFilter);

    }

    private void initFloating() {
        getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                if (!LklPreferences.getInstance()
                        .getBoolean(UniqueKey.FLOATINGLAYER_SHOWED, false)) {
                    // 是否显示过浮层
                    Intent intent = new Intent(context, FloatingLayerActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
        });
    }

    private void initSplash() {
        new SplashTask(this).execute();
    }

    /**
     * 获取ic降级开关
     */
    private void getIcDownEnable() {
        ShoudanService.getInstance().getIcDownEnable(new ServiceResultCallback() {
            @Override
            public void onSuccess(ResultServices resultServices) {
                JSONObject jb = null;
                try {
                    jb = new JSONObject(resultServices.retData);
                    ApplicationEx.getInstance().getUser().getAppConfig().setIcDownEnabled(jb.optJSONObject("IC_DOWN_ENABLED").optBoolean("flag", false));
                } catch (Exception e) {
                    LogUtil.print(e);
                }
            }

            @Override
            public void onEvent(HttpConnectEvent connectEvent) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if (lastClickTime == 0) {
            lastClickTime = System.currentTimeMillis();
            ToastUtil.toast(this, getString(R.string.press_back_again_and_exit));
            return;
        }

        final long interval = System.currentTimeMillis() - lastClickTime;

        lastClickTime = System.currentTimeMillis();

        if (interval > 2000) {
            ToastUtil.toast(this, getString(R.string.press_back_again_and_exit));
        } else {
            ApplicationEx.getInstance().exit();
        }
    }

    /**
     * 听云SDK自定义事件统计测试
     */

    public void testTingyun(){
        ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                ArrayList<String>  list=new ArrayList<>();
                for (int i=0;i<1500;i++){
                    list.add("听云测试:"+i);
                }
                while (true){
                    Random random=new Random();
                    int index=random.nextInt(1500);
                    NBSAppAgent.onEvent(list.get(index));
                    LogUtil.print("tingyun",list.get(index));
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}
